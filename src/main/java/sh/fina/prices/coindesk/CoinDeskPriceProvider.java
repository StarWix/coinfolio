package sh.fina.prices.coindesk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sh.fina.entities.Price;
import sh.fina.prices.PriceProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "prices", havingValue = "coindesk")
@AllArgsConstructor
@Slf4j
public class CoinDeskPriceProvider implements PriceProvider {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper;

    @Override
    public List<String> findCurrencySymbols() {
        throw new NotImplementedException("findCurrencySymbols");
    }

    @Override
    public List<Price> findPrices(final Instant startDate,
                                  final Instant endDate,
                                  final String assetSymbol,
                                  final String currencySymbol) {
        final String url = String.format("https://min-api.cryptocompare.com/data/v2/histoday?fsym=%s&tsym=%s&allData=true",
                assetSymbol, currencySymbol);

        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (final Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected code " + response);
            }

            final JsonNode root = objectMapper.readTree(response.body().string());

            final List<Price> prices = new ArrayList<>();
            for (final JsonNode node : root.path("Data").path("Data")) {
                final long time = node.path("time").asLong();
                final Instant timestamp = Instant.ofEpochSecond(time);

                if (!timestamp.isBefore(startDate) && !timestamp.isAfter(endDate)) {
                    final BigDecimal close = node.path("close").decimalValue();

                    final Price price = new Price(
                            currencySymbol,
                            ChronoUnit.DAYS,
                            assetSymbol,
                            timestamp,
                            close
                    );

                    prices.add(price);
                }
            }
            return prices;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
