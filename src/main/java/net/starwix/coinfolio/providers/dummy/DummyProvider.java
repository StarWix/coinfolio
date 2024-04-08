package net.starwix.coinfolio.providers.dummy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.starwix.coinfolio.entities.*;
import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Provider;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DummyProvider implements Provider<DummyProvider.Meta> {
    private final ReadonlyProviderConfig config;
    private final List<Transaction> transactions;
    private final Direction direction;

    public DummyProvider(final ReadonlyProviderConfig config) {
        this.config = config;
        direction = Direction.valueOf(config.getProperties().getOrDefault("direction", "ASC"));

        final BigDecimal btcAmount = BigDecimal.valueOf(10_000. / 44187).setScale(8, RoundingMode.HALF_EVEN);
        final var transactions = List.of(
                Transaction.builder()
                        .id(new Transaction.Id("dummy", "1", config.getId()))
                        .actions(List.of(
                                Action.builder()
                                        .accountId(config.getId() + ":USDT")
                                        .sender(new Subject("dummy"))
                                        .recipient(new Subject("dummy", config.getId() + ":USDT", config.getId()))
                                        .amount(BigDecimal.valueOf(10_002))
                                        .assetSymbol("USDT")
                                        .type(ActionType.TRANSFER)
                                        .build()
                        ))
                        .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
                        .status(TransactionStatus.COMPLETED)
                        .note("Deposit")
                        .childId(new Transaction.Id("tron", "832b0be39c472400b4edde257619d151ae34e1fed4f474bce04f11dc2f611250"))
                        .build(),
                Transaction.builder()
                        .id(new Transaction.Id("dummy", "2", config.getId()))
                        .actions(List.of(
                                Action.builder()
                                        .accountId(config.getId() + ":USDT")
                                        .sender(new Subject("dummy", config.getId() + ":USDT", config.getId()))
                                        .recipient(new Subject("dummy"))
                                        .amount(BigDecimal.valueOf(-10_000))
                                        .assetSymbol("USDT")
                                        .type(ActionType.TRANSFER)
                                        .build(),
                                Action.builder()
                                        .accountId(config.getId() + ":USDT")
                                        .sender(new Subject("dummy", config.getId() + ":USDT", config.getId()))
                                        .recipient(new Subject("dummy"))
                                        .amount(BigDecimal.valueOf(-1))
                                        .assetSymbol("USDT")
                                        .type(ActionType.FEE)
                                        .build(),
                                Action.builder()
                                        .accountId(config.getId() + ":BTC")
                                        .sender(new Subject("dummy"))
                                        .recipient(new Subject("dummy", config.getId() + ":BTC", config.getId()))
                                        .amount(btcAmount)
                                        .assetSymbol("BTC")
                                        .type(ActionType.TRANSFER)
                                        .build()
                        ))
                        .createdAt(Instant.parse("2024-01-02T00:00:00Z"))
                        .status(TransactionStatus.COMPLETED)
                        .note("Buy bitcoin")
                        .build(),
                Transaction.builder()
                        .id(new Transaction.Id("dummy", "3", config.getId()))
                        .actions(List.of(
                                Action.builder()
                                        .accountId(config.getId() + ":BTC")
                                        .sender(new Subject("dummy", config.getId() + ":BTC", config.getId()))
                                        .recipient(new Subject("dummy"))
                                        .amount(btcAmount.negate())
                                        .assetSymbol("BTC")
                                        .type(ActionType.TRANSFER)
                                        .build(),
                                Action.builder()
                                        .accountId(config.getId() + ":USDT")
                                        .sender(new Subject("dummy", config.getId() + ":USDT", config.getId()))
                                        .recipient(new Subject("dummy"))
                                        .amount(BigDecimal.valueOf(-1))
                                        .assetSymbol("USDT")
                                        .type(ActionType.FEE)
                                        .build(),
                                Action.builder()
                                        .accountId(config.getId() + ":USDT")
                                        .sender(new Subject("dummy"))
                                        .recipient(new Subject("dummy", config.getId() + ":USDT", config.getId()))
                                        .amount(btcAmount.multiply(new BigDecimal(42569).setScale(2, RoundingMode.HALF_EVEN)))
                                        .assetSymbol("USDT")
                                        .type(ActionType.TRANSFER)
                                        .build()
                        ))
                        .createdAt(Instant.parse("2024-02-01T00:00:00Z"))
                        .status(TransactionStatus.COMPLETED)
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
    public int getId() {
        return config.getId();
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private Instant instant;
    }
}
