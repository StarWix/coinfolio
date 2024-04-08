package net.starwix.coinfolio.providers.dummy;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.ProviderFactory;
import org.springframework.stereotype.Component;

@Component
public class DummyProviderFactory implements ProviderFactory {
    @Override
    public String getSource() {
        return "dummy";
    }

    @Override
    public DummyProvider create(final ReadonlyProviderConfig config) {
        return new DummyProvider(config);
    }
}
