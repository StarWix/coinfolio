package net.starwix.coinfolio.commands;

import lombok.AllArgsConstructor;
import net.starwix.coinfolio.services.TransactionService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent()
@AllArgsConstructor
public class TransactionCommands {
    private final TransactionService transactionService;

    @ShellMethod(key = "transactions pull")
    public void pull() {
        transactionService.pull();
    }
}
