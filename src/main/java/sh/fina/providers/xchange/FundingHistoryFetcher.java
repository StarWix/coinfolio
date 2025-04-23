package sh.fina.providers.xchange;

import org.jetbrains.annotations.Nullable;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import sh.fina.entities.*;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public abstract class FundingHistoryFetcher<M> implements Fetcher<M> {
    protected final int providerConfigId;
    protected final String source;
    protected final FundingRecord.Type fundingRecordType;
    protected final String type;
    protected final AccountService accountService;

    public FundingHistoryFetcher(final ReadonlyProviderConfig config,
                                 final AccountService accountService,
                                 final FundingRecord.Type fundingRecordType) {
        providerConfigId = config.getId();
        source = config.getSource();
        this.fundingRecordType = fundingRecordType;
        this.accountService = accountService;
        this.type = "funding-history-" + fundingRecordType.name().toLowerCase();
    }

    @Override
    public int getProviderConfigId() {
        return providerConfigId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<Account> findAccounts() {
        throw new UnsupportedOperationException("findAccounts");
    }

    @Override
    public Direction getDirection() {
        return Direction.ASC;
    }

    @Override
    public TransactionList<M> findTransactions(@Nullable M meta) {
        final TradeHistoryParams params = convert(meta);
        final List<FundingRecord> fundingRecords;
        try {
            fundingRecords = accountService.getFundingHistory(params);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        final List<Transaction> transactions = fundingRecords.stream().map(this::convert).toList();
        final M resultMeta = convert(transactions, meta);

        return new TransactionList<>(transactions, resultMeta);
    }

    private Transaction convert(final FundingRecord fundingRecord) {
        final String accountId = providerConfigId + ":" + fundingRecord.getCurrency().getCurrencyCode();
        final boolean isSender = fundingRecordType.isOutflowing();
        Subject sender = new Subject(source, accountId, providerConfigId);
        Subject recipient = new Subject();
        if (!isSender) {
            Subject t = sender;
            sender = recipient;
            recipient = t;
        }
        final Action transfer = Action.builder()
                .accountId(accountId)
                .sender(sender)
                .recipient(recipient)
                .amount(isSender ? fundingRecord.getAmount().negate() : fundingRecord.getAmount())
                .assetSymbol(fundingRecord.getCurrency().getCurrencyCode())
                .type(ActionType.TRANSFER)
                .build();
        final Action fee = fundingRecord.getFee().compareTo(BigDecimal.ZERO) == 0
                ? null
                : Action.builder()
                        .accountId(accountId)
                        .sender(sender)
                        .recipient(recipient)
                        .amount(fundingRecord.getFee().negate())
                        .assetSymbol(fundingRecord.getCurrency().getCurrencyCode())
                        .type(ActionType.FEE)
                        .build();
        return Transaction.builder()
                .id(new Transaction.Id(source, fundingRecord.getInternalId(), providerConfigId))
                .actions(fee == null ? List.of(transfer) : List.of(transfer, fee))
                .createdAt(fundingRecord.getDate().toInstant())
                .status(convert(fundingRecord.getStatus()))
                .childId(isSender && fundingRecord.getBlockchainTransactionHash() != null
                        ? new Transaction.Id("unknown", fundingRecord.getBlockchainTransactionHash())
                        : null
                ).build();
    }

    private TransactionStatus convert(final FundingRecord.Status status) {
        return switch (status) {
            case PROCESSING -> TransactionStatus.PROCESSING;
            case CANCELLED -> TransactionStatus.CANCELED;
            case COMPLETE -> TransactionStatus.COMPLETED;
            case FAILED -> TransactionStatus.ERROR;
        };
    }

    abstract protected TradeHistoryParams convert(@Nullable M meta);
    abstract protected M convert(List<Transaction> transactions, @Nullable M prevMeta);
}
