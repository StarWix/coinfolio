package sh.fina.prices.multisource;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import sh.fina.entities.Price;
import sh.fina.prices.PriceProvider;
import sh.fina.prices.coindesk.CoinDeskPriceProvider;
import sh.fina.prices.coingecko.CoinGeckoPriceProvider;

import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
public class MultisourcePriceProvider implements PriceProvider {
    private final CoinGeckoPriceProvider coinGeckoPriceProvider;
    private final CoinDeskPriceProvider coinDeskPriceProvider;

    @Override
    public List<String> findCurrencySymbols() {
        throw new NotImplementedException("findCurrencySymbols");
    }

    @Override
    public List<Price> findPrices(final Instant startDate,
                                  final Instant endDate,
                                  String assetSymbol,
                                  final String currencySymbol) {
        final PriceProvider priceProvider = switch (assetSymbol) {
            case "SUSDE", "WSTETH", "USDT0" -> coinGeckoPriceProvider;
            default -> coinDeskPriceProvider;
        };
        return priceProvider.findPrices(startDate, endDate, assetSymbol, currencySymbol);
    }
}
