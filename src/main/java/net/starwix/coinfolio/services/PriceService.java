package net.starwix.coinfolio.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import net.starwix.coinfolio.entities.Price;
import net.starwix.coinfolio.providers.prices.PriceProvider;
import net.starwix.coinfolio.repositories.ActionRepository;
import net.starwix.coinfolio.repositories.PriceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class PriceService {
    private final PriceProvider priceProvider;
    private final PriceRepository priceRepository;
    private final ActionRepository actionRepository;

    public void pull(final String currencySymbol) {
        final List<String> assetSymbols = actionRepository.findDistinctAssetSymbol();
        final Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        for (final String assetSymbol : assetSymbols) {
            final Optional<Price> lastPrice =
                    priceRepository.findTop1ByAssetSymbolAndCurrencySymbolOrderByDateDesc(assetSymbol, currencySymbol);
            final Instant lastPriceDate = lastPrice.map(Price::getDate).orElse(Instant.MIN);
            if (lastPriceDate.truncatedTo(ChronoUnit.DAYS).equals(now)) {
                log.info("Prices were already cached. AssetSymbol: {}, CurrencySymbol: {}", assetSymbol, currencySymbol);
                continue;
            }
            final List<Price> prices = priceProvider.findPrices(Instant.MIN, now, assetSymbol, currencySymbol);
            priceRepository.saveAll(prices);
            log.info("Prices were updated. AssetSymbol: {}, CurrencySymbol: {}", assetSymbol, currencySymbol);
        }
    }
}
