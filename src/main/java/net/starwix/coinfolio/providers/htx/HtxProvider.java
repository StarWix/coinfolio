package net.starwix.coinfolio.providers.htx;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Fetcher;
import net.starwix.coinfolio.providers.Provider;
import net.starwix.coinfolio.providers.xchange.NumericIdFundingHistoryFetcher;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.huobi.HuobiExchange;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HtxProvider implements Provider {
    @Override
    public String getSource() {
        return "htx";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        final var exSpec = new HuobiExchange().getDefaultExchangeSpecification();
        exSpec.setApiKey(config.getProperty("apiKey"));
        exSpec.setSecretKey(config.getProperty("secretKey"));
        final var exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
        final var accountService = exchange.getAccountService();
        return List.of(
                new NumericIdFundingHistoryFetcher(config, accountService, FundingRecord.Type.DEPOSIT),
                new NumericIdFundingHistoryFetcher(config, accountService, FundingRecord.Type.WITHDRAWAL)
        );
    }
}
