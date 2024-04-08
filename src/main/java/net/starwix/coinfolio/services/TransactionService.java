package net.starwix.coinfolio.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.starwix.coinfolio.entities.Action;
import net.starwix.coinfolio.entities.ProviderConfig;
import net.starwix.coinfolio.entities.Transaction;
import net.starwix.coinfolio.entities.TransactionStatus;
import net.starwix.coinfolio.providers.Provider;
import net.starwix.coinfolio.repositories.ProviderConfigRepository;
import net.starwix.coinfolio.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@AllArgsConstructor
@Service
@Log4j2
public class TransactionService {
    private final ProviderService providerService;
    private final TransactionRepository transactionRepository;
    private final ProviderConfigRepository providerConfigRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public void pull() {
        log.info("Pull called");
        final List<Provider<?>> providers = providerService.findAll();
        for (final var provider : providers) {
            final ProviderConfig config = providerConfigRepository.findById(provider.getId())
                    .orElseThrow(() -> new IllegalStateException("ProviderId: " + provider.getId()));

            if (provider.getDirection() == Provider.Direction.ASC) {
                processTransactionsAsc(provider, config);
            } else if (provider.getDirection() == Provider.Direction.DESC) {
                if (config.getLastMeta() != null) {
                    processTransactionsDesc(provider, config);
                }
                processTransactionsDesc(provider, config);
            } else {
                throw new IllegalStateException(provider.getDirection().name());
            }
        }
    }

    private <M> void processTransactionsDesc(final Provider<M> provider, final ProviderConfig config) {
        final Transaction.Id lastTransactionIdForProcessing =
                transactionRepository.findTop1ById_ProviderConfigIdAndStatusOrderByCreatedAtAsc(provider.getId(), TransactionStatus.PROCESSING)
                        .or(() -> transactionRepository.findTop1ById_ProviderConfigIdAndStatusOrderByCreatedAtDesc(provider.getId(), TransactionStatus.COMPLETED))
                        .map(Transaction::getId)
                        .orElse(null);

        while (true) {
            final var transactions = getTransactionsAndUpdateMeta(provider, config);
            final boolean foundLastTransactionIdForProcessing =
                    transactions.getTransactions().stream().anyMatch(x -> x.getId().equals(lastTransactionIdForProcessing));
            if (foundLastTransactionIdForProcessing) {
                config.setLastMeta(null);
            }
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                transactionRepository.saveAll(transactions.getTransactions());
                providerConfigRepository.save(config);
            });
            if (foundLastTransactionIdForProcessing || transactions.getNextPageMeta() == null) {
                break;
            }
        }
    }

    private <M> void processTransactionsAsc(final Provider<M> provider, final ProviderConfig config) {
        boolean needSaveConfig = true;

        while (true) {
            final var transactions = getTransactionsAndUpdateMeta(provider, config);
            transactionRepository.saveAll(transactions.getTransactions());
            needSaveConfig = needSaveConfig
                    && transactions.getNextPageMeta() != null
                    && transactions.getTransactions().stream().allMatch(x -> x.getStatus().isTerminated());
            if (needSaveConfig) {
                providerConfigRepository.save(config);
            }
            if (transactions.getNextPageMeta() == null) {
                break;
            }
        }
    }

    private <M> Provider.TransactionList<M> getTransactionsAndUpdateMeta(final Provider<M> provider,
                                                                         final ProviderConfig config) {
        M meta;
        try {
            meta = config.getLastMeta() == null ? null : objectMapper.readValue(config.getLastMeta(), provider.getMetaClass());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        final Provider.TransactionList<M> transactions = provider.findTransactions(meta);
        for (Transaction transaction : transactions.getTransactions()) {
            for (Action action : transaction.getActions()) {
                action.setTransaction(transaction);
            }
        }
        try {
            config.setLastMeta(objectMapper.writeValueAsString(transactions.getNextPageMeta()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return transactions;
    }
}
