package sh.fina.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sh.fina.entities.Price;
import sh.fina.prices.multisource.MultisourcePriceProvider;
import sh.fina.repositories.ActionRepository;
import sh.fina.repositories.PriceRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class PriceService {
    private final MultisourcePriceProvider priceProvider;
    private final PriceRepository priceRepository;
    private final ActionRepository actionRepository;

    public void pull(final String currencySymbol, final ChronoUnit timeframe) {
        final List<String> assetSymbols = actionRepository.findDistinctAssetSymbol();
        final Instant now = Instant.now().truncatedTo(timeframe);
        for (final String assetSymbol : assetSymbols) {
            final Optional<Price> lastPrice =
                    priceRepository.findTop1ByCurrencySymbolAndTimeframeAndAssetSymbolOrderByOpenTimestampDesc(currencySymbol, timeframe, assetSymbol);
            final Instant lastPriceDate = lastPrice.map(Price::getOpenTimestamp).orElse(Instant.MIN);
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
