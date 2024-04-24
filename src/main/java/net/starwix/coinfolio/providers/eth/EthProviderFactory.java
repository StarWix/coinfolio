package net.starwix.coinfolio.providers.eth;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.ProviderFactory;
import org.springframework.stereotype.Component;

@Component
public class EthProviderFactory implements ProviderFactory {
    @Override
    public String getSource() {
        return "eth";
    }

    @Override
    public EthProvider create(ReadonlyProviderConfig config) {
        return new EthProvider(config);
    }
}
