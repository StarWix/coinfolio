package net.starwix.coinfolio.commands;

import lombok.AllArgsConstructor;
import net.starwix.coinfolio.services.PriceService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent()
@AllArgsConstructor
public class PriceCommands {
    private final PriceService priceService;

    @ShellMethod(key = "prices pull")
    public void pull(final @ShellOption(defaultValue = "USD") String symbol) {
        priceService.pull(symbol);
    }
}
