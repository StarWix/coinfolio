package net.starwix.coinfolio.providers;

import net.starwix.coinfolio.entities.Account;
import net.starwix.coinfolio.entities.Transaction;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

public interface Provider {
    int getId();
    List<Account> findAccounts();

    /**
     * Finds transactions based on the provided start date.
     *
     * @param startDate The start date for which transactions should be retrieved. Can be null.
     * @return A list of transactions in ascending order that match the given start date.
     */
    List<Transaction> findTransactions(@Nullable Instant startDate);
}
