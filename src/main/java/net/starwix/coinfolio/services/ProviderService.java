package net.starwix.coinfolio.services;

import net.starwix.coinfolio.entities.ProviderConfig;
import net.starwix.coinfolio.providers.Provider;
import net.starwix.coinfolio.providers.ProviderFactory;
import net.starwix.coinfolio.repositories.ProviderConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProviderService {
    private final ProviderConfigRepository providerConfigRepository;
    private final Map<String, ProviderFactory> providerFactoriesBySource;

    public ProviderService(final ProviderConfigRepository providerConfigRepository,
                           final List<ProviderFactory> providerFactories) {
        this.providerConfigRepository = providerConfigRepository;
        this.providerFactoriesBySource = providerFactories.stream()
                .collect(Collectors.toMap(ProviderFactory::getSource, x -> x));
    }

    public List<Provider> findAll() {
        final List<ProviderConfig> providerConfigs = providerConfigRepository.findAll();
        return providerConfigs.stream()
                .map(config -> {
                    final var factory = providerFactoriesBySource.get(config.getSource());
                    if (factory == null) {
                        return null;
                    }
                    return factory.create(config);
                })
                .filter(Objects::nonNull) // TODO: warnings if provider is missing
                .toList();
    }
}
