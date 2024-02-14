package net.starwix.coinfolio.repositories;

import net.starwix.coinfolio.entities.ProviderConfig;
import net.starwix.coinfolio.entities.Transaction;
import net.starwix.coinfolio.entities.TransactionStatus;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    void saveAll(final List<Transaction> transactions);
    Optional<Transaction> findTop1ByProviderAndStatusOrderByCreatedAtDesc(ProviderConfig providerConfig, TransactionStatus status);
    Optional<Transaction> findTop1ByProviderAndStatusOrderByCreatedAtAsc(ProviderConfig providerConfig, TransactionStatus status);
}
