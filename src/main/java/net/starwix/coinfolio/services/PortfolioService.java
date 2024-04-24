package net.starwix.coinfolio.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.starwix.coinfolio.entities.Price;
import net.starwix.coinfolio.entities.Transaction;
import net.starwix.coinfolio.models.Statistic;
import net.starwix.coinfolio.repositories.PriceRepository;
import net.starwix.coinfolio.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class PortfolioService {
    private final PriceRepository priceRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public List<Statistic> overview(final String currencyAsset) {
        final Iterable<Price> prices = priceRepository.findByCurrencySymbolOrderByDateAsc(currencyAsset);
        final Iterable<Transaction> transactions = transactionRepository.findAllByOrderByCreatedAtAsc();

        final Calculator calculator = new Calculator(prices.iterator(), transactions.iterator());
        return calculator.calc();
    }

    private static class Calculator {
        private final Map<String, BigDecimal> amountBySymbol = new HashMap<>();
        private final Map<String, BigDecimal> priceBySymbol = new HashMap<>();
        private final Iterator<Price> priceIt;
        private final Iterator<Transaction> transactionIt;

        private Price price;
        private Instant date;

        public Calculator(final Iterator<Price> priceIt,
                          final Iterator<Transaction> transactionIt) {
            this.priceIt = priceIt;
            this.transactionIt = transactionIt;
        }

        private List<Statistic> calc() {
            if (!priceIt.hasNext() || !transactionIt.hasNext()) {
                return List.of();
            }
            price = priceIt.next();
            Transaction transaction = transactionIt.next();
            date = transaction.getCreatedAt().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
            updatePrices();

            final List<Statistic> statistics = new ArrayList<>();
            while (transaction != null) {
                while (!date.isAfter(transaction.getCreatedAt())) {
                    statistics.add(calcStatistic());
                }
                for (final var action : transaction.getActions()) {
                    amountBySymbol.merge(action.getAssetSymbol(), action.getAmount(), BigDecimal::add);
                }
                transaction = transactionIt.hasNext() ? transactionIt.next() : null;
            }
            final Instant now = Instant.now();
            while (!date.isAfter(now)) {
                statistics.add(calcStatistic());
            }
            return statistics;
        }

        private Statistic calcStatistic() {
            final BigDecimal total = priceBySymbol.entrySet().stream().map(entry -> {
                BigDecimal amount = amountBySymbol.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                return entry.getValue().multiply(amount);
            }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            final Statistic statistic = new Statistic(date, total);
            date = date.plus(1, ChronoUnit.DAYS);
            updatePrices();
            return statistic;
        }

        private void updatePrices() {
            while (!price.getDate().isAfter(date)) {
                priceBySymbol.put(price.getAssetSymbol(), price.getPrice());
                if (!priceIt.hasNext()) {
                    break;
                }
                price = priceIt.next();
            }
        }
    }


}
