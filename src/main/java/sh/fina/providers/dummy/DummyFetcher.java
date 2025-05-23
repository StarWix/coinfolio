package sh.fina.providers.dummy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sh.fina.entities.*;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DummyFetcher implements Fetcher<DummyFetcher.Meta> {
    private final ReadonlyProviderConfig config;
    private final List<Transaction> transactions;
    private final Direction direction;

    public DummyFetcher(final ReadonlyProviderConfig config) {
        this.config = config;
        direction = Direction.valueOf(config.getProperties().getOrDefault("direction", "ASC"));

        // 2025-01-01T00:00:00Z Deposit 10_002 USDT
        // 2025-01-02T00:00:00Z Buy BTC (0.10591393) on 10_000 USDT AND pay fee 1 USDT.
        // 2025-02-01T00:00:00Z Sell all BTC (0.10591393) and pay fee 1 USDT.
        final BigDecimal btcAmount = BigDecimal.valueOf(10_000. / 94416.29).setScale(8, RoundingMode.HALF_EVEN);
        final String accountId = "" + config.getId();
        final var transactions = List.of(
                Transaction.builder()
                        .id(new Transaction.Id("dummy", "1", config.getId()))
                        .actions(List.of(
                                Action.builder()
                                        .sender(new Subject("dummy"))
                                        .recipient(new Subject("dummy", accountId))
                                        .amount(BigDecimal.valueOf(10_002))
                                        .assetSymbol("USDT")
                                        .type(Action.Type.TRANSFER)
                                        .build()
                        ))
                        .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                        .status(Transaction.Status.COMPLETED)
                        .note("Deposit")
                        .childId(new Transaction.Id("tron", "832b0be39c472400b4edde257619d151ae34e1fed4f474bce04f11dc2f611250"))
                        .build(),
                Transaction.builder()
                        .id(new Transaction.Id("dummy", "2", config.getId()))
                        .actions(List.of(
                                Action.builder()
                                        .sender(new Subject("dummy", accountId))
                                        .recipient(new Subject("dummy"))
                                        .amount(BigDecimal.valueOf(10_000))
                                        .assetSymbol("USDT")
                                        .type(Action.Type.TRANSFER)
                                        .build(),
                                Action.builder()
                                        .sender(new Subject("dummy", accountId))
                                        .recipient(new Subject("dummy"))
                                        .amount(BigDecimal.valueOf(1))
                                        .assetSymbol("USDT")
                                        .type(Action.Type.FEE)
                                        .build(),
                                Action.builder()
                                        .sender(new Subject("dummy"))
                                        .recipient(new Subject("dummy", accountId))
                                        .amount(btcAmount)
                                        .assetSymbol("BTC")
                                        .type(Action.Type.TRANSFER)
                                        .build()
                        ))
                        .createdAt(Instant.parse("2025-01-02T00:00:00Z"))
                        .status(Transaction.Status.COMPLETED)
                        .note("Buy bitcoin")
                        .build(),
                Transaction.builder()
                        .id(new Transaction.Id("dummy", "3"))
                        .actions(List.of(
                                Action.builder()
                                        .sender(new Subject("dummy", accountId))
                                        .recipient(new Subject("dummy"))
                                        .amount(btcAmount)
                                        .assetSymbol("BTC")
                                        .type(Action.Type.TRANSFER)
                                        .build(),
                                Action.builder()
                                        .sender(new Subject("dummy", accountId))
                                        .recipient(new Subject("dummy"))
                                        .amount(BigDecimal.valueOf(1))
                                        .assetSymbol("USDT")
                                        .type(Action.Type.FEE)
                                        .build(),
                                Action.builder()
                                        .sender(new Subject("dummy"))
                                        .recipient(new Subject("dummy", accountId))
                                        .amount(btcAmount.multiply(new BigDecimal(100674).setScale(2, RoundingMode.HALF_EVEN)))
                                        .assetSymbol("USDT")
                                        .type(Action.Type.TRANSFER)
                                        .build()
                        ))
                        .createdAt(Instant.parse("2025-02-01T00:00:00Z"))
                        .status(Transaction.Status.COMPLETED)
                        .note("Sell bitcoin")
                        .build()
        );
        if (direction == Direction.DESC) {
            this.transactions = transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                    .collect(Collectors.toList());
        } else {
            this.transactions = transactions;
        }
    }

    @Override
    public int getProviderConfigId() {
        return config.getId();
    }

    @Override
    public String getType() {
        return "dummy";
    }

    @Override
    public List<Account> findAccounts() {
        throw new UnsupportedOperationException("findAccounts");
    }


    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Class<Meta> getMetaClass() {
        return Meta.class;
    }

    @Override
    public TransactionList<Meta> findTransactions(@Nullable Meta meta) {
        final List<Transaction> resultTransactions;
        if (meta == null) {
            resultTransactions = transactions.subList(0, 2);
        } else {
            resultTransactions = transactions.stream()
                    .filter(transaction -> direction == Direction.ASC ? !transaction.getCreatedAt().isBefore(meta.getInstant()): !transaction.getCreatedAt().isAfter(meta.getInstant()))
                    .limit(2)
                    .collect(Collectors.toList());
        }
        final Meta resultMeta = resultTransactions.isEmpty() ? null : new Meta(resultTransactions.getLast().getCreatedAt().plusMillis(direction == Direction.ASC ? 1 : -1));
        return new TransactionList<>(resultTransactions, resultMeta);
    }

    @Override
    public boolean owns(Subject subject) {
        return ("" + config.getId()).equals(subject.getAccountId());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private Instant instant;
    }
}
