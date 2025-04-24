package sh.fina.providers.eth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sh.fina.entities.*;
import sh.fina.external.blockscount.client.api.DefaultApi;
import sh.fina.external.blockscount.model.api.InternalTransaction;
import sh.fina.external.blockscount.model.api.TokenTransfer;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
abstract public class AbstractFetcher implements Fetcher<AbstractFetcher.Meta> {
    protected static final int ETH_DECIMALS = 18;

    protected final DefaultApi api;
    protected final String publicKey;
    protected final int id;
    protected final String source;

    public AbstractFetcher(final ReadonlyProviderConfig config) {
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

    @Override
    public boolean owns(final Subject subject) {
        return publicKey.equals(subject.getAccountId());
    }

    protected Transaction convert(final sh.fina.external.blockscount.model.api.Transaction transaction) {
        final List<Action> actions = new ArrayList<>();

        actions.add(Action.builder()
                .type(Action.Type.FEE)
                .amount(new BigDecimal(transaction.getFee().getValue()).scaleByPowerOfTen(-ETH_DECIMALS))
                .assetSymbol("ETH")
                .sender(new Subject("eth", transaction.getFrom().getHash()))
                .recipient(new Subject("eth"))
                .build());
        if (transaction.getTransactionTypes().contains(sh.fina.external.blockscount.model.api.Transaction.TransactionTypesEnum.COIN_TRANSFER)) {
            actions.add(Action.builder()
                    .type(Action.Type.TRANSFER)
                    .amount(new BigDecimal(transaction.getValue()).scaleByPowerOfTen(-ETH_DECIMALS))
                    .assetSymbol("ETH")
                    .sender(new Subject("eth", transaction.getFrom().getHash()))
                    .recipient(new Subject("eth", transaction.getTo().getHash()))
                    .build());
        }
        if (transaction.getTransactionTypes().contains(sh.fina.external.blockscount.model.api.Transaction.TransactionTypesEnum.TOKEN_TRANSFER)) {
            final var transfers = api.getTransactionTokenTransfers(transaction.getHash(), null);
            var tokenActions = transfers.getItems().stream()
                    .map(this::convert)
                    .filter(Objects::nonNull)
                    .toList();
            actions.addAll(tokenActions);
            if (transfers.getNextPageParams() != null) {
                log.warn("Transaction {} has second page of transfers", transaction.getHash());
            }
        }
        final var internalTransactions = api.getTransactionInternalTxs(transaction.getHash());
        var internalActions = internalTransactions.getItems().stream()
                .map(this::convert)
                .toList();
        actions.addAll(internalActions);
        if (internalTransactions.getNextPageParams() != null) {
            log.warn("Transaction {} has second page of internal transfers", transaction.getHash());
        }

        return Transaction.builder()
                .id(new Transaction.Id(source, transaction.getHash(), id))
                .createdAt(transaction.getTimestamp().toInstant())
                .status(convert(transaction.getStatus()))
                .note(transaction.getResult())
                .actions(actions)
                .build();
    }

    private Transaction.Status convert(final sh.fina.external.blockscount.model.api.Transaction.StatusEnum status) {
        return switch (status) {
            case OK -> Transaction.Status.COMPLETED;
            case ERROR -> Transaction.Status.ERROR;
        };
    }

    private Action convert(final TokenTransfer tokenTransfer) {
        if (tokenTransfer.getTotal().getDecimals() == null) {
            return null;
        }
        final int decimals = Integer.parseInt(tokenTransfer.getTotal().getDecimals());
        final BigDecimal amount = new BigDecimal(tokenTransfer.getTotal().getValue()).scaleByPowerOfTen(-decimals);
        return Action.builder()
                .type(Action.Type.TRANSFER)
                .amount(amount)
                .assetSymbol(tokenTransfer.getToken().getSymbol())
                .sender(new Subject("eth", tokenTransfer.getFrom().getHash()))
                .recipient(new Subject("eth", tokenTransfer.getTo().getHash()))
                .build();
    }

    private Action convert(final InternalTransaction internalTransaction) {
        return Action.builder()
                .type(Action.Type.TRANSFER)
                .amount(new BigDecimal(internalTransaction.getValue()).scaleByPowerOfTen(-ETH_DECIMALS))
                .assetSymbol("ETH")
                .sender(new Subject("eth", internalTransaction.getFrom().getHash()))
                .recipient(new Subject("eth", internalTransaction.getTo().getHash()))
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private Map<String, String> nextPageParams;
    }
}
