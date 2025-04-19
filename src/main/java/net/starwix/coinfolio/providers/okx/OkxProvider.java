package net.starwix.coinfolio.providers.okx;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Fetcher;
import net.starwix.coinfolio.providers.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OkxProvider implements Provider {
    @Override
    public String getSource() {
        return "okx";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        return List.of(

        );
    }
}
