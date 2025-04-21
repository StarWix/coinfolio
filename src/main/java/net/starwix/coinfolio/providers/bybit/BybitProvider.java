package net.starwix.coinfolio.providers.bybit;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Fetcher;
import net.starwix.coinfolio.providers.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BybitProvider implements Provider {
    @Override
    public String getSource() {
        return "bybit";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        return List.of(
            // https://github.com/knowm/XChange/issues/5026
        );
    }
}
