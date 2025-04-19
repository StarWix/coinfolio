package net.starwix.coinfolio.providers.xchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.starwix.coinfolio.entities.Transaction;
import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import org.jetbrains.annotations.Nullable;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.HistoryParamsFundingType;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsIdSpan;

import java.util.List;
import java.util.stream.Stream;

public class NumericIdFundingHistoryFetcher extends FundingHistoryFetcher<NumericIdFundingHistoryFetcher.Meta> {
    public NumericIdFundingHistoryFetcher(final ReadonlyProviderConfig config,
                                          final AccountService accountService,
                                          final FundingRecord.Type fundingRecordType) {
        super(config, accountService, fundingRecordType);
    }

    @Override
    public Class<Meta> getMetaClass() {
        return Meta.class;
    }

    @Override
    protected TradeHistoryParams convert(final @Nullable Meta meta) {
        return new NumericIdTradeHistoryParams(
                fundingRecordType,
                meta == null ? "1" : "" + meta.getNextTransactionId()
        );
    }

    @Override
    protected Meta convert(final List<Transaction> transactions, final @Nullable Meta meta) {
        if (transactions.isEmpty()) {
            return null;
        }
        return new Meta(Long.parseLong(transactions.getLast().getId().getTransactionId()) + 1);
    }

    public static List<NumericIdFundingHistoryFetcher> buildAll(final ReadonlyProviderConfig config,
                                                                final AccountService accountService) {
        return Stream.of(FundingRecord.Type.values())
                .map(type -> new NumericIdFundingHistoryFetcher(config, accountService, type))
                .toList();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private long nextTransactionId;
    }

    @Data
    @AllArgsConstructor
    private static class NumericIdTradeHistoryParams implements HistoryParamsFundingType, TradeHistoryParamsIdSpan {
        private FundingRecord.Type type;
        private String startId;

        @Override
        public String getEndId() {
            return null;
        }

        @Override
        public void setEndId(String endId) {
        }
    }
}
