package net.starwix.coinfolio.providers;

import net.starwix.coinfolio.entities.ProviderConfig;

public interface ProviderFactory {
    String getSource();
    Provider create(ProviderConfig config);
}
