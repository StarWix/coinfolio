package net.starwix.coinfolio.providers.binance;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Fetcher;
import net.starwix.coinfolio.providers.Provider;
import net.starwix.coinfolio.providers.xchange.DateFundingHistoryFetcher;
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
        final var exSpec = new BinanceExchange().getDefaultExchangeSpecification();
        exSpec.setApiKey(config.getProperty("apiKey"));
        exSpec.setSecretKey(config.getProperty("secretKey"));
        final var exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
        final var accountService = exchange.getAccountService();
        return List.of(
                new DateFundingHistoryFetcher(config, accountService, FundingRecord.Type.DEPOSIT),
                new DateFundingHistoryFetcher(config, accountService, FundingRecord.Type.WITHDRAWAL),
                new DateFundingHistoryFetcher(config, accountService, FundingRecord.Type.OTHER_INFLOW)
        );
    }
}
