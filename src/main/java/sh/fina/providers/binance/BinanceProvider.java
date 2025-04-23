package sh.fina.providers.binance;

import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;
import sh.fina.providers.Provider;
import sh.fina.providers.xchange.DateFundingHistoryFetcher;
import sh.fina.providers.xchange.ProviderHelper;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.dto.account.FundingRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BinanceProvider implements Provider {
    @Override
    public String getSource() {
        return "binance";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        final var exchangeSpecification = ProviderHelper.toExchangeSpecification(config, BinanceExchange.class);
        if ("true".equalsIgnoreCase(config.getProperties().get("sandbox"))) {
            exchangeSpecification.setSslUri(BinanceExchange.SANDBOX_SPOT_URL);
        }
        final var exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        final var accountService = exchange.getAccountService();
        return List.of(
                new DateFundingHistoryFetcher(config, accountService, FundingRecord.Type.DEPOSIT),
                new DateFundingHistoryFetcher(config, accountService, FundingRecord.Type.WITHDRAWAL),
                new DateFundingHistoryFetcher(config, accountService, FundingRecord.Type.OTHER_INFLOW)
        );
    }
}
