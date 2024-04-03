package net.starwix.coinfolio.providers.prices;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.CoinList;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import net.starwix.coinfolio.entities.Price;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CoinGeckoPriceProvider implements PriceProvider {
    private final CoinGeckoApiClient client;
    private final Map<String, String> idBySymbol;

    public CoinGeckoPriceProvider() {
        client = new CoinGeckoApiClientImpl();
//        idBySymbol = client.getCoinList().stream()
//                .collect(Collectors.toMap(CoinList::getSymbol, CoinList::getId, (s, s2) -> s));
        idBySymbol = new HashMap<>();
        idBySymbol.put("usdt", "tether");
        idBySymbol.put("btc", "bitcoin");
        idBySymbol.put("eth", "ethereum");
    }

    @Override
    public List<String> findCurrencySymbols() {
        return client.getSupportedVsCurrencies().stream().map(String::toUpperCase).toList();
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
        return client.getCoinMarketChartById(id, currencySymbol.toLowerCase(), 365, "daily").getPrices().stream()
                .map(x -> new Price(
                        assetSymbol,
                        Instant.ofEpochMilli(Long.parseLong(x.get(0))),
                        currencySymbol,
                        new BigDecimal(x.get(1))
                ))
                .filter(x -> !x.getDate().isBefore(startDate) && !x.getDate().isAfter(endDate))
                .filter(x -> x.getDate().toEpochMilli() % ChronoUnit.DAYS.getDuration().toMillis() == 0)
                .toList();
    }
}
