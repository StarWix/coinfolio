package net.starwix.coinfolio.repositories;

import net.starwix.coinfolio.entities.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Price.Id> {
    Optional<Price> findTop1ByCurrencySymbolAndTimeframeAndAssetSymbolOrderByOpenTimestampDesc(final String currencySymbol, final ChronoUnit timeframe, final String assetSymbol);
    List<Price> findByCurrencySymbolAndTimeframeOrderByOpenTimestampAsc(final String currencySymbol, final ChronoUnit timeframe);
}
