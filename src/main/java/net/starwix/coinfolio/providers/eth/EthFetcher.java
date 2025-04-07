package net.starwix.coinfolio.providers.eth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.starwix.blockscount.client.api.DefaultApi;
import net.starwix.blockscount.model.api.TokenTransfer;
import net.starwix.coinfolio.entities.*;
import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Fetcher;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class EthFetcher implements Fetcher<EthFetcher.Meta> {
    private static final int ETH_DECIMALS = 18;

    private final DefaultApi api;
    private final String publicKey;
    private final int id;
    private final String source;

    public EthFetcher(final ReadonlyProviderConfig config) {
        api = new DefaultApi();
        publicKey = config.getProperties().get("publicKey");
        id = config.getId();
        source = config.getSource();
    }

    @Override
    public int getProviderConfigId() {
        return id;
    }

    @Override
    public String getType() {
        return "eth";
    }

    @Override
    public List<Account> findAccounts() {
        throw new UnsupportedOperationException("findAccounts");
    }

    @Override
    public Direction getDirection() {
        return Direction.DESC;
    }

    @Override
    public Class<Meta> getMetaClass() {
        return Meta.class;
    }

    @Override
    public TransactionList<Meta> findTransactions(@Nullable final Meta meta) {
        final var nextPageParams = meta == null ? null : meta.getNextPageParams();
        final var blockchainTransactions = api.getAddressTxs(publicKey, null, nextPageParams);

        final var transactions = blockchainTransactions.getItems().stream().map(this::convert).toList();

        return new TransactionList<>(
                transactions,
                blockchainTransactions.getNextPageParams() == null ? null : new Meta(blockchainTransactions.getNextPageParams())
        );
    }

    private BigDecimal negateIfSender(final BigDecimal amount, final boolean isSender) {
        return isSender ? amount.negate() : amount;
    }

    private Transaction convert(final net.starwix.blockscount.model.api.Transaction transaction) {
        final List<Action> actions = new ArrayList<>();
        final boolean isSender = publicKey.equals(transaction.getFrom().getHash());

        if (isSender) {
            actions.add(Action.builder()
                    .type(ActionType.FEE)
                    .accountId(publicKey + ":ETH")
                    .amount(new BigDecimal(transaction.getFee().getValue()).scaleByPowerOfTen(-ETH_DECIMALS).negate())
                    .assetSymbol("ETH")
                    .sender(new Subject("eth", publicKey + ":ETH", id))
                    .recipient(new Subject("eth"))
                    .build());
        }
        if (transaction.getTransactionTypes().contains(net.starwix.blockscount.model.api.Transaction.TransactionTypesEnum.COIN_TRANSFER)) {
             actions.add(Action.builder()
                    .type(ActionType.TRANSFER)
                    .accountId(publicKey + ":ETH")
                    .amount(negateIfSender(new BigDecimal(transaction.getValue()).scaleByPowerOfTen(-ETH_DECIMALS), isSender))
                    .assetSymbol("ETH")
                    .sender(new Subject("eth", transaction.getFrom().getHash(), id))
                    .recipient(new Subject("eth", transaction.getTo().getHash(), id))
                    .build());
        }
        if (transaction.getTransactionTypes().contains(net.starwix.blockscount.model.api.Transaction.TransactionTypesEnum.TOKEN_TRANSFER)) {
            final var transfers = api.getTransactionTokenTransfers(transaction.getHash(), null);
            var tokenActions = transfers.getItems().stream().map(tokenTransfer -> convert(tokenTransfer, isSender))
                    .filter(Objects::nonNull)
                    .toList();
            actions.addAll(tokenActions);
            if (transfers.getNextPageParams() != null) {
                log.warn("Transaction {} has second page of transfers", transaction.getHash());
            }
        }

        return Transaction.builder()
                .id(new Transaction.Id(source, transaction.getHash(), id))
                .createdAt(transaction.getTimestamp().toInstant())
                .status(convert(transaction.getStatus()))
                .note(transaction.getResult())
                .actions(actions)
                .build();
    }

    private TransactionStatus convert(final net.starwix.blockscount.model.api.Transaction.StatusEnum status) {
        return switch (status) {
            case OK -> TransactionStatus.COMPLETED;
            case ERROR -> TransactionStatus.ERROR;
            default -> throw new IllegalStateException(status.name());
        };
    }

    private Action convert(final TokenTransfer tokenTransfer, final boolean isSender) {
        if (tokenTransfer.getTotal().getDecimals() == null) {
            return null;
        }
        final int decimals = Integer.parseInt(tokenTransfer.getTotal().getDecimals());
        final BigDecimal amount = new BigDecimal(tokenTransfer.getTotal().getValue()).scaleByPowerOfTen(-decimals);
        return Action.builder()
                .type(ActionType.TRANSFER)
                .accountId(publicKey + ":" + tokenTransfer.getToken().getSymbol())
                .amount(negateIfSender(amount, isSender))
                .assetSymbol(tokenTransfer.getToken().getSymbol())
                .sender(new Subject("eth", tokenTransfer.getFrom().getHash(), id))
                .recipient(new Subject("eth", tokenTransfer.getTo().getHash(), id))
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private Map<String, String> nextPageParams;
    }
}
