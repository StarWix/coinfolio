package net.starwix.coinfolio.commands;

import lombok.AllArgsConstructor;
import net.starwix.coinfolio.services.PortfolioService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@ShellComponent()
@AllArgsConstructor
public class PortfolioCommands {
    private final PortfolioService portfolioService;

    @ShellMethod(key = "describe portfolio")
    public String describe(final @ShellOption(defaultValue = "USD") String symbol,
                           final @ShellOption(defaultValue = "DAYS") ChronoUnit timeframe) {
        return portfolioService.overview(symbol, timeframe).stream()
                .map(statistic -> String.format(
                        "%10s: %20.8f",
                        DateTimeFormatter.ISO_DATE.format(statistic.date().atOffset(ZoneOffset.UTC)),
                        statistic.amount()
                ))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
