package sh.fina.prices;

import sh.fina.entities.Price;

import java.time.Instant;
import java.util.List;

public interface PriceProvider {
    List<String> findCurrencySymbols();

    /**
     * Finds daily prices within a specified range of dates and for a specific asset symbol and currency.
     *
     * @param startDate      The start date of the price range (inclusive).
     * @param endDate        The end date of the price range (inclusive).
     * @param assetSymbol    The symbol of the asset for which prices should be found.
     * @param currencySymbol The currency in which prices should be returned.
     * @return A list of daily prices sorted in ascending order by date.
     */
    List<Price> findPrices(Instant startDate, Instant endDate, String assetSymbol, String currencySymbol);
}
