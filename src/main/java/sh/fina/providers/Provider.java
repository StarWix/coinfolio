package sh.fina.providers;

import sh.fina.models.ReadonlyProviderConfig;

import java.util.List;

public interface Provider {
    String getSource();
    List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config);
}
        