package net.starwix.coinfolio;

import net.starwix.coinfolio.services.OverviewService;
import net.starwix.coinfolio.services.PriceService;
import net.starwix.coinfolio.services.TransactionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoinfolioApplication {
    public static void main(String[] args) {
        final var context = SpringApplication.run(CoinfolioApplication.class, args);
        context.getBean(TransactionService.class).pull();
        context.getBean(PriceService.class).pull("USD");
        context.getBean(OverviewService.class).overview("USD");
    }
}
