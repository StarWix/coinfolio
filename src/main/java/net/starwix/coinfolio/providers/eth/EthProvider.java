package net.starwix.coinfolio.providers.eth;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;
import net.starwix.coinfolio.providers.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EthProvider implements Provider {
    @Override
    public String getSource() {
        return "eth";
    }

    @Override
    public List<EthFetcher> createFetchers(ReadonlyProviderConfig config) {
        return List.of(new EthFetcher(config));
    }
}
