package sh.fina.providers.btc;

import lombok.RequiredArgsConstructor;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;
import sh.fina.providers.Provider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BtcProvider implements Provider {
    private final RestClient restClient;

    @Override
    public String getSource() {
        return "btc";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        return List.of(new BtcFetcher(config, restClient));
    }
}
