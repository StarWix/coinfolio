package net.starwix.coinfolio.repositories;

import net.starwix.coinfolio.entities.Asset;
import net.starwix.coinfolio.entities.Transaction;
import net.starwix.coinfolio.entities.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Transaction.Id> {
    Optional<Transaction> findTop1ById_ProviderConfigIdAndStatusOrderByCreatedAtDesc(int providerConfigId, TransactionStatus status);
    Optional<Transaction> findTop1ById_ProviderConfigIdAndStatusOrderByCreatedAtAsc(int providerConfigId, TransactionStatus status);
    Iterable<Transaction> findAllByOrderByCreatedAtAsc();
}
