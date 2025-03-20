package net.starwix.coinfolio.services;

import net.starwix.coinfolio.entities.ProviderConfig;
import net.starwix.coinfolio.providers.Fetcher;
import net.starwix.coinfolio.providers.Provider;
import net.starwix.coinfolio.repositories.ProviderConfigRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FetcherService {
    private final ProviderConfigRepository providerConfigRepository;
    private final Map<String, Provider> providersBySource;

    public FetcherService(final ProviderConfigRepository providerConfigRepository,
                          final List<Provider> providerFactories) {
        this.providerConfigRepository = providerConfigRepository;
        this.providersBySource = providerFactories.stream()
                .collect(Collectors.toMap(Provider::getSource, x -> x));
    }

    public List<Fetcher<?>> findAll() {
        final List<ProviderConfig> providerConfigs = providerConfigRepository.findAll();
        final List<? extends Fetcher<?>> providers = providerConfigs.stream()
                .flatMap(config -> {
                    final var provider = providersBySource.get(config.getSource());
                    if (provider == null) {
                        return null;
                    }
                    return provider.createFetchers(config).stream();
                })
                .filter(Objects::nonNull) // TODO: warnings if provider is missing
                .toList();
        return new ArrayList<>(providers);
    }
}
