package net.starwix.coinfolio.providers;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;

public interface ProviderFactory {
    String getSource();
    Provider<?> create(ReadonlyProviderConfig config);
}
