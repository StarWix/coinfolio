package net.starwix.coinfolio.repositories;

import net.starwix.coinfolio.entities.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Price.Id> {
    Optional<Price> findTop1ByAssetSymbolAndCurrencySymbolOrderByDateDesc(final String assetSymbol, final String currencySymbol);
    List<Price> findByCurrencySymbolOrderByDateAsc(final String currencySymbol);
}
