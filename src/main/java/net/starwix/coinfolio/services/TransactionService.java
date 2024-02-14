package net.starwix.coinfolio.services;

import lombok.AllArgsConstructor;
import net.starwix.coinfolio.entities.ConfiguredProvider;
import net.starwix.coinfolio.entities.Transaction;
import net.starwix.coinfolio.entities.TransactionStatus;
import net.starwix.coinfolio.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Service
public class TransactionService {
    private final ProviderService providerService;
    private final TransactionRepository transactionRepository;

    public void pull() {
        final List<ConfiguredProvider> providers = providerService.findAll();
        for (final var provider : providers) {
            final var lastCompletedTransaction =
                    transactionRepository.findTop1ByProviderAndStatusOrderByCreatedAtDesc(provider.config(), TransactionStatus.COMPLETED);
            final var firstProcessingTransaction =
                    transactionRepository.findTop1ByProviderAndStatusOrderByCreatedAtAsc(provider.config(), TransactionStatus.PROCESSING);

            Instant lastDate = firstProcessingTransaction.isPresent()
                    ? firstProcessingTransaction.get().getCreatedAt()
                    : lastCompletedTransaction.map(Transaction::getCreatedAt).orElse(null);

            while (true) {
                final var transactions = provider.implementation().findTransactions(provider.config(), lastDate);
                transactionRepository.saveAll(transactions);
                if (transactions.size() <= 1) {
                    break;
                }
                lastDate = transactions.getLast().getCreatedAt();
            }

        }
    }
}
