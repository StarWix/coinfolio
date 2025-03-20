package net.starwix.coinfolio.providers.dummy;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DummyProvider implements Provider {
    @Override
    public String getSource() {
        return "dummy";
    }

    @Override
    public List<DummyFetcher> createFetchers(final ReadonlyProviderConfig config) {
        return List.of(new DummyFetcher(config));
    }
}
