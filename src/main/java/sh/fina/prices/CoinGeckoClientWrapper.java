package sh.fina.prices;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.CoinMarkets;
import com.litesoftwares.coingecko.domain.Coins.MarketChart;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Retry(name = "coingecko")
@RateLimiter(name = "coingecko")
public class CoinGeckoClientWrapper {
    private static final int MARKETS_PER_PAGE_LIMIT = 250;

    private final CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

    @Retry(name = "coingecko")
    @RateLimiter(name = "coingecko")
    public List<CoinMarkets> getCoinMarkets(final int page) {
        return client.getCoinMarkets("USD", null, null, MARKETS_PER_PAGE_LIMIT, page, false, "1h");
    }

    @Retry(name = "coingecko")
    @RateLimiter(name = "coingecko")
    public List<String> getSupportedVsCurrencies() {
        return client.getSupportedVsCurrencies();
    }

    @Retry(name = "coingecko")
    @RateLimiter(name = "coingecko")
    public MarketChart getCoinMarketChartById(final String id, final String currencySymbol) {
        return client.getCoinMarketChartById(id, currencySymbol, 365, "daily");
    }
}
