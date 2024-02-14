package net.starwix.coinfolio.providers;

import net.starwix.coinfolio.entities.Account;
import net.starwix.coinfolio.entities.ProviderConfig;
import net.starwix.coinfolio.entities.Transaction;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

public interface Provider {
    String getType();
    List<Account> findAccounts(final ProviderConfig config);
    List<Transaction> findTransactions(final ProviderConfig config, @Nullable Instant startDate); // provider должен возвращать транзакции по возрастанию.
}
