package sh.fina.providers.htx;

import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.huobi.HuobiExchange;
import org.springframework.stereotype.Component;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;
import sh.fina.providers.Provider;
import sh.fina.providers.xchange.NumericIdFundingHistoryFetcher;
import sh.fina.providers.xchange.ProviderHelper;

import java.util.List;

@Component
public class HtxProvider implements Provider {
    @Override
    public String getSource() {
        return "htx";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        final var exchangeSpecification = ProviderHelper.toExchangeSpecification(config, HuobiExchange.class);
        final var exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        final var accountService = exchange.getAccountService();
        return List.of(
                new NumericIdFundingHistoryFetcher(config, accountService, FundingRecord.Type.DEPOSIT),
                new NumericIdFundingHistoryFetcher(config, accountService, FundingRecord.Type.WITHDRAWAL)
        );
    }
}
