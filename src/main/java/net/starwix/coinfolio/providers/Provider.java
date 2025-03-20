package net.starwix.coinfolio.providers;

import net.starwix.coinfolio.models.ReadonlyProviderConfig;

import java.util.List;

public interface Provider {
    String getSource();
    List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config);
}
        