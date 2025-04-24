package sh.fina.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import sh.fina.entities.Price;
import sh.fina.entities.Transaction;
import sh.fina.models.Statistic;
import sh.fina.repositories.PriceRepository;
import sh.fina.repositories.TransactionRepository;
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
    public List<Statistic> overview(final String currencyAsset, final ChronoUnit timeframe) {
        final Iterable<Price> prices = priceRepository.findByCurrencySymbolAndTimeframeOrderByOpenTimestampAsc(currencyAsset, timeframe);
        final Iterable<Transaction> transactions = transactionRepository.findAllByOrderByCreatedAtAsc();

        final Calculator calculator = new Calculator(prices.iterator(), transactions.iterator(), timeframe);
        return calculator.calc();
    }

    private static class Calculator {
        private final Map<String, BigDecimal> amountBySymbol = new HashMap<>();
        private final Map<String, BigDecimal> priceBySymbol = new HashMap<>();
        private final Iterator<Price> priceIt;
        private final Iterator<Transaction> transactionIt;
        private final ChronoUnit timeframe;

        private Price price;
        private Instant currentOpenTimestamp;

        public Calculator(final Iterator<Price> priceIt,
                          final Iterator<Transaction> transactionIt,
                          final ChronoUnit timeframe) {
            this.priceIt = priceIt;
            this.transactionIt = transactionIt;
            this.timeframe = timeframe;
        }

        private List<Statistic> calc() {
            if (!priceIt.hasNext() || !transactionIt.hasNext()) {
                return List.of();
            }
            price = priceIt.next();
            Transaction transaction = transactionIt.next();
            currentOpenTimestamp = transaction.getCreatedAt().truncatedTo(timeframe);
            updatePrices();

            final List<Statistic> statistics = new ArrayList<>();
            while (transaction != null) {
                while (!transaction.getCreatedAt().isBefore(currentOpenTimestamp.plus(1, timeframe))) {
                    statistics.add(calcStatistic());
                    currentOpenTimestamp = currentOpenTimestamp.plus(1, timeframe);
                    updatePrices();
                }
                for (final var action : transaction.getActions()) {
                    amountBySymbol.merge(action.getAssetSymbol(), action.getPortfolioAmount(), BigDecimal::add);
                }
                transaction = transactionIt.hasNext() ? transactionIt.next() : null;
            }
            final Instant now = Instant.now();
            while (!currentOpenTimestamp.isAfter(now)) {
                statistics.add(calcStatistic());
                currentOpenTimestamp = currentOpenTimestamp.plus(1, timeframe);
                updatePrices();
            }
            return statistics;
        }

        private Statistic calcStatistic() {
            final BigDecimal total = priceBySymbol.entrySet().stream().map(entry -> {
                BigDecimal amount = amountBySymbol.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                return entry.getValue().multiply(amount);
            }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            return new Statistic(currentOpenTimestamp, total);
        }

        private void updatePrices() {
            while (!price.getOpenTimestamp().isAfter(currentOpenTimestamp)) {
                priceBySymbol.put(price.getAssetSymbol(), price.getClosePrice());
                if (!priceIt.hasNext()) {
                    break;
                }
                price = priceIt.next();
            }
        }
    }


}
