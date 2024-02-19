package net.starwix.coinfolio.services;

import net.starwix.coinfolio.entities.ConfiguredProvider;
import net.starwix.coinfolio.entities.ProviderConfig;
import net.starwix.coinfolio.providers.Provider;
import net.starwix.coinfolio.repositories.ProviderConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProviderService {
    private final ProviderConfigRepository providerConfigRepository;
    private final Map<String, Provider> providerImplementations;

    public ProviderService(final ProviderConfigRepository providerConfigRepository,
                           final List<Provider> providers) {
        this.providerConfigRepository = providerConfigRepository;
        this.providerImplementations = providers.stream()
                .collect(Collectors.toMap(Provider::getSource, x -> x));
    }

    public List<ConfiguredProvider> findAll() {
        final List<ProviderConfig> providerConfigs = providerConfigRepository.findAll();
        return providerConfigs.stream()
                .map(config -> new ConfiguredProvider(config, providerImplementations.get(config.getSource())))
                .filter(provider -> provider.implementation() != null) // TODO: warnings if provider is missing
                .toList();
    }
}
