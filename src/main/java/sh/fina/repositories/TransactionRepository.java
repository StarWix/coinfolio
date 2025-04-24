package sh.fina.repositories;

import sh.fina.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Transaction.Id> {
    @Query("""
           SELECT t
           FROM Transaction t
           WHERE t.id.providerConfigId = :providerConfigId AND t.id.providerSource = :providerSource AND t.status = :status
           ORDER BY t.createdAt DESC
           LIMIT 1
           """)
    Optional<Transaction> findNewestBy(int providerConfigId, String providerSource, Transaction.Status status);

    @Query("""
           SELECT t
           FROM Transaction t
           WHERE t.id.providerConfigId = :providerConfigId AND t.id.providerSource = :providerSource AND t.status = :status
           ORDER BY t.createdAt ASC
           LIMIT 1
           """)
    Optional<Transaction> findOldestBy(int providerConfigId, String providerSource, Transaction.Status status);

    Iterable<Transaction> findAllByOrderByCreatedAtAsc();
}
