package sh.fina.services;

import lombok.extern.slf4j.Slf4j;
import sh.fina.entities.ProviderConfig;
import sh.fina.providers.Fetcher;
import sh.fina.providers.Provider;
import sh.fina.repositories.ProviderConfigRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
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
                    log.info("Creating fetchers for {}", config);
                    final var provider = providersBySource.get(config.getSource());
                    if (provider == null) {
                        return null;
                    }
                    return provider.createFetchers(config).stream();
                })
                .filter(Objects::nonNull) // TODO: warnings if provider is missing
                .toList();
        log.info("Found {} fetchers", providers.size());
        return new ArrayList<>(providers);
    }
}
