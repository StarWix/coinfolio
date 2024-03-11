package net.starwix.coinfolio;

import net.starwix.coinfolio.services.OverviewService;
import net.starwix.coinfolio.services.PriceService;
import net.starwix.coinfolio.services.TransactionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class CoinfolioApplication {
    public static void main(String[] args) {
        final var context = SpringApplication.run(CoinfolioApplication.class, args);
        context.getBean(TransactionService.class).pull();
        context.getBean(PriceService.class).pull("USD");
        final var statistics = context.getBean(OverviewService.class).overview("USD");
        for (final var statistic : statistics) {
            System.out.printf("%10s: %20.8f%n",
                    DateTimeFormatter.ISO_DATE.format(statistic.date().atOffset(ZoneOffset.UTC)), statistic.amount());
        }
    }
}
