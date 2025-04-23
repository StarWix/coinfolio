package sh.fina.providers;

import lombok.Value;
import sh.fina.entities.Account;
import sh.fina.entities.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Fetcher<M> {
    int getProviderConfigId();
    String getType();
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
