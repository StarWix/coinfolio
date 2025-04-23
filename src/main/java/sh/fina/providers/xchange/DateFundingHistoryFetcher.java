package sh.fina.providers.xchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sh.fina.entities.Transaction;
import sh.fina.models.ReadonlyProviderConfig;
import org.jetbrains.annotations.Nullable;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.HistoryParamsFundingType;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsTimeSpan;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class DateFundingHistoryFetcher extends FundingHistoryFetcher<DateFundingHistoryFetcher.Meta> {
    private static final Instant BITCOIN_LAUNCH_DATE = Instant.parse("2009-01-03T00:00:00Z");

    public DateFundingHistoryFetcher(final ReadonlyProviderConfig config,
                                     final AccountService accountService,
                                     final FundingRecord.Type fundingRecordType) {
        super(config, accountService, fundingRecordType);
    }

    @Override
    protected TradeHistoryParams convert(final @Nullable Meta meta) {
        Instant startTime = meta == null ? BITCOIN_LAUNCH_DATE : meta.getNextDate();
        return new DateTradeHistoryParams(
                fundingRecordType,
                Date.from(startTime),
                Date.from(nextDate(startTime))
        );
    }

    private Instant nextDate(final Instant date) {
        return date.plus(90, ChronoUnit.DAYS);
    }

    @Override
    protected Meta convert(final List<Transaction> list, final @Nullable Meta prevMeta) {
        final Instant startTime = (prevMeta == null ? BITCOIN_LAUNCH_DATE : prevMeta.getNextDate());
        if (list.isEmpty() || list.getLast().getCreatedAt().compareTo(startTime) == 0) {
            final Instant nextDate = nextDate(startTime);
            if (nextDate.isAfter(Instant.now())) {
                return null;
            }
            return new Meta(nextDate);
        }
        return new Meta(list.getLast().getCreatedAt());
    }

    @Override
    public Class<Meta> getMetaClass() {
        return Meta.class;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private Instant nextDate;
    }

    @Data
    @AllArgsConstructor
    private static class DateTradeHistoryParams implements HistoryParamsFundingType, TradeHistoryParamsTimeSpan {
        private FundingRecord.Type type;
        private Date startTime;
        private Date endTime;
    }
}
