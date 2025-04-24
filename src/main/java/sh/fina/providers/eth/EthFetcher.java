package sh.fina.providers.eth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sh.fina.entities.*;
import sh.fina.external.blockscount.client.api.DefaultApi;
import sh.fina.external.blockscount.model.api.TokenTransfer;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
abstract public class EthFetcher implements Fetcher<EthFetcher.Meta> {
    protected static final int ETH_DECIMALS = 18;

    protected final DefaultApi api;
    protected final String publicKey;
    protected final int id;
    protected final String source;

    public EthFetcher(final ReadonlyProviderConfig config) {
        api = new DefaultApi();
        publicKey = config.getAddress();
        id = config.getId();
        source = config.getSource();
    }

    @Override
    public int getProviderConfigId() {
        return id;
    }

    @Override
    public List<Account> findAccounts() {
        throw new UnsupportedOperationException("findAccounts");
    }

    @Override
    public Fetcher.Direction getDirection() {
        return Fetcher.Direction.DESC;
    }

    @Override
    public Class<Meta> getMetaClass() {
        return Meta.class;
    }

    private BigDecimal negateIfSender(final BigDecimal amount, final boolean isSender) {
        return isSender ? amount.negate() : amount;
    }

    protected Transaction convert(final sh.fina.external.blockscount.model.api.Transaction transaction) {
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
        if (transaction.getTransactionTypes().contains(sh.fina.external.blockscount.model.api.Transaction.TransactionTypesEnum.COIN_TRANSFER)) {
            actions.add(Action.builder()
                    .type(ActionType.TRANSFER)
                    .accountId(publicKey + ":ETH")
                    .amount(negateIfSender(new BigDecimal(transaction.getValue()).scaleByPowerOfTen(-ETH_DECIMALS), isSender))
                    .assetSymbol("ETH")
                    .sender(new Subject("eth", transaction.getFrom().getHash(), id))
                    .recipient(new Subject("eth", transaction.getTo().getHash(), id))
                    .build());
        }
        if (transaction.getTransactionTypes().contains(sh.fina.external.blockscount.model.api.Transaction.TransactionTypesEnum.TOKEN_TRANSFER)) {
            final var transfers = api.getTransactionTokenTransfers(transaction.getHash(), null);
            var tokenActions = transfers.getItems().stream().map(tokenTransfer -> convert(tokenTransfer))
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

    private TransactionStatus convert(final sh.fina.external.blockscount.model.api.Transaction.StatusEnum status) {
        return switch (status) {
            case OK -> TransactionStatus.COMPLETED;
            case ERROR -> TransactionStatus.ERROR;
            default -> throw new IllegalStateException(status.name());
        };
    }

    private Action convert(final TokenTransfer tokenTransfer) {
        if (tokenTransfer.getTotal().getDecimals() == null) {
            return null;
        }
        final boolean isSender = publicKey.equals(tokenTransfer.getFrom().getHash());
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
