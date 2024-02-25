package net.starwix.coinfolio.providers.dummy;

import net.starwix.coinfolio.entities.ProviderConfig;
import net.starwix.coinfolio.providers.Provider;
import net.starwix.coinfolio.providers.ProviderFactory;
import org.springframework.stereotype.Component;

@Component
public class DummyProviderFactory implements ProviderFactory {
    @Override
    public String getSource() {
        return "dummy";
    }

    @Override
    public Provider create(final ProviderConfig config) {
        return new DummyProvider(config);
    }
}
