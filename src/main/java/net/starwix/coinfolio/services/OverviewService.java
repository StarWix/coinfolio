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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class OverviewService {
    private final PriceRepository priceRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public List<Statistic> overview(final String currencyAsset) {
        final Iterable<Price> prices = priceRepository.findByCurrencySymbolOrderByDateAsc(currencyAsset);
        final Iterable<Transaction> transactions = transactionRepository.findAllByOrderByCreatedAtAsc();
        final var priceIt = prices.iterator();
        final var transactionIt = transactions.iterator();
        final Map<String, BigDecimal> amountBySymbol = new HashMap<>();

        while (transactionIt.hasNext()) {
            final var transaction = transactionIt.next();
            for (final var action : transaction.getActions()) {
                amountBySymbol.merge(action.getAssetSymbol(), action.getAmount(), BigDecimal::add);
                System.out.println(amountBySymbol);
            }
        }
        return List.of();
    }
}
