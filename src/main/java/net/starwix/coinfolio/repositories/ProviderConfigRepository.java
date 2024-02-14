package net.starwix.coinfolio.repositories;

import net.starwix.coinfolio.entities.ProviderConfig;

import java.util.List;

public interface ProviderConfigRepository {
    List<ProviderConfig> findAll();
}
