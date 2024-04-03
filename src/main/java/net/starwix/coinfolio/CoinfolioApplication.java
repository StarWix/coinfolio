package net.starwix.coinfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.starwix.coinfolio.services.OverviewService;
import net.starwix.coinfolio.services.PriceService;
import net.starwix.coinfolio.services.TransactionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
