package net.starwix.coinfolio;

import jakarta.persistence.criteria.CriteriaBuilder;
import net.starwix.coinfolio.providers.prices.PriceProvider;
import net.starwix.coinfolio.services.TransactionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;

@SpringBootApplication
public class CoinfolioApplication {
    public static void main(String[] args) {
        final var context = SpringApplication.run(CoinfolioApplication.class, args);
        context.getBean(TransactionService.class).pull();
        System.out.println(context.getBean(PriceProvider.class).findPrices(
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.now(),
                "btc",
                "usd"
        ));
    }
}
