package net.starwix.coinfolio.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.starwix.coinfolio.entities.*;
import net.starwix.coinfolio.providers.Fetcher;
import net.starwix.coinfolio.repositories.FetcherStateRepository;
import net.starwix.coinfolio.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@AllArgsConstructor
@Service
@Log4j2
public class TransactionService {
    private final FetcherService fetcherService;
    private final TransactionRepository transactionRepository;
    private final FetcherStateRepository metaRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public void pull() {
        log.info("Pull called");
        final List<Fetcher<?>> fetchers = fetcherService.findAll();
        for (final var fetcher : fetchers) {
            final FetcherState fetcherState = metaRepository.findById(new FetcherState.Id(fetcher.getProviderConfigId(), fetcher.getType()))
                    .orElseGet(() -> new FetcherState(fetcher.getProviderConfigId(), fetcher.getType(), null));
            if (fetcher.getDirection() == Fetcher.Direction.ASC) {
                processTransactionsAsc(fetcher, fetcherState);
            } else if (fetcher.getDirection() == Fetcher.Direction.DESC) {
                if (fetcherState.getMeta() != null) {
                    processTransactionsDesc(fetcher, fetcherState);
                }
                processTransactionsDesc(fetcher, fetcherState);
            } else {
                throw new IllegalStateException(fetcher.getDirection().name());
            }
        }
    }

    private <M> void processTransactionsDesc(final Fetcher<M> fetcher, final FetcherState fetcherState) {
        final Transaction.Id lastTransactionIdForProcessing =
                transactionRepository.findNewestBy(fetcher.getProviderConfigId(), fetcher.getType(), TransactionStatus.PROCESSING)
                        .or(() -> transactionRepository.findOldestBy(fetcher.getProviderConfigId(), fetcher.getType(), TransactionStatus.COMPLETED))
                        .map(Transaction::getId)
                        .orElse(null);

        while (true) {
            final var transactions = getTransactionsAndUpdateMeta(fetcher, fetcherState);
            final boolean foundLastTransactionIdForProcessing =
                    transactions.getTransactions().stream().anyMatch(x -> x.getId().equals(lastTransactionIdForProcessing));
            if (foundLastTransactionIdForProcessing) {
                fetcherState.setMeta(null);
            }
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                transactionRepository.saveAll(transactions.getTransactions());
                metaRepository.save(fetcherState);
            });
            if (foundLastTransactionIdForProcessing || transactions.getNextPageMeta() == null) {
                break;
            }
        }
    }

    private <M> void processTransactionsAsc(final Fetcher<M> fetcher, final FetcherState fetcherState) {
        boolean needSaveConfig = true;

        while (true) {
            final var transactions = getTransactionsAndUpdateMeta(fetcher, fetcherState);
            transactionRepository.saveAll(transactions.getTransactions());
            needSaveConfig = needSaveConfig
                    && transactions.getNextPageMeta() != null
                    && transactions.getTransactions().stream().allMatch(x -> x.getStatus().isTerminated());
            if (needSaveConfig) {
                metaRepository.save(fetcherState);
            }
            if (transactions.getNextPageMeta() == null) {
                break;
            }
        }
    }

    private <M> Fetcher.TransactionList<M> getTransactionsAndUpdateMeta(final Fetcher<M> fetcher,
                                                                        final FetcherState fetcherState) {
        M lastMeta;
        try {
            lastMeta = fetcherState.getMeta() == null ? null : objectMapper.readValue(fetcherState.getMeta(), fetcher.getMetaClass());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        final Fetcher.TransactionList<M> transactions = fetcher.findTransactions(lastMeta);
        for (Transaction transaction : transactions.getTransactions()) {
            for (Action action : transaction.getActions()) {
                action.setTransaction(transaction);
            }
        }
        try {
            fetcherState.setMeta(objectMapper.writeValueAsString(transactions.getNextPageMeta()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return transactions;
    }
}
