package net.starwix.coinfolio.entities;

import net.starwix.coinfolio.providers.Provider;

public record ConfiguredProvider(ProviderConfig config, Provider implementation) {
}
