package net.starwix.coinfolio.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import net.starwix.coinfolio.entities.Account;
import net.starwix.coinfolio.entities.Transaction;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public interface Provider<M> {
    int getId();
    List<Account> findAccounts();
    Direction getDirection();
    Class<M> getMetaClass();

    /**
     * Finds transactions based on the provided meta.
     *
     * @param meta The meta for which transactions should be retrieved. Can be null.
     */
    TransactionList<M> findTransactions(@Nullable M meta);

    @Value
    class TransactionList<M> {
        List<Transaction> transactions;
        /**
         * Null if provider doesn't have transactions.
         */
        @Nullable M nextPageMeta;
    }

    enum Direction {
        ASC,
        DESC
    }
}
