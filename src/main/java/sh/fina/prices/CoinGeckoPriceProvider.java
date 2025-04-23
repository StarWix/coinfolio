package sh.fina.prices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sh.fina.entities.Price;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "prices", havingValue = "coingecko")
@Slf4j
public class CoinGeckoPriceProvider implements PriceProvider {
    private final Map<String, String> idBySymbol;
    private final CoinGeckoClientWrapper client;

    private static final int PAGES = 5;

    public CoinGeckoPriceProvider(final CoinGeckoClientWrapper client) {
        this.client = client;

        idBySymbol = new LinkedHashMap<>();
        for (int i = 1; i <= PAGES; i++) {
            for (var market : client.getCoinMarkets(i)) {
                idBySymbol.merge(market.getSymbol(), market.getId(), (prev, next) -> {
                    log.info("Duplicate symbol. {} -> {}", prev, next);
                    return prev;
                });
            }
        }
        final var symbols = idBySymbol.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
        log.info("CoinGeckoPriceProvider created with symbols:\n{}", symbols);
    }

    @Override
    public List<String> findCurrencySymbols() {
        return client.getSupportedVsCurrencies();
    }

    @Override
    public List<Price> findPrices(final Instant startDate,
                                  final Instant endDate,
                                  final String assetSymbol,
                                  final String currencySymbol) {
        final String id = idBySymbol.get(assetSymbol.toLowerCase());
        if (id == null) {
            throw new IllegalArgumentException(String.format("AssetSymbol %s is not found", assetSymbol));
        }
        return client.getCoinMarketChartById(id, currencySymbol.toLowerCase()).getPrices().stream()
                .map(x -> new Price(
                        currencySymbol,
                        ChronoUnit.DAYS,
                        assetSymbol,
                        Instant.ofEpochMilli(Long.parseLong(x.get(0))).minus(1, ChronoUnit.DAYS),
                        new BigDecimal(x.get(1))
                ))
                .filter(x -> !x.getOpenTimestamp().isBefore(startDate) && !x.getOpenTimestamp().isAfter(endDate))
                .filter(x -> x.getOpenTimestamp().toEpochMilli() % ChronoUnit.DAYS.getDuration().toMillis() == 0)
                .toList();
    }
}
