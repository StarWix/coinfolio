package sh.fina.commands;

import lombok.AllArgsConstructor;
import sh.fina.services.TransactionService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent()
@AllArgsConstructor
public class TransactionCommands {
    private final TransactionService transactionService;

    @ShellMethod(key = "pull transactions")
    public void pull() {
        transactionService.pull();
    }
}
