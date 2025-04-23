package sh.fina.providers.bybit;

import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;
import sh.fina.providers.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BybitProvider implements Provider {
    @Override
    public String getSource() {
        return "bybit";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        return List.of(
            // https://github.com/knowm/XChange/issues/5026
        );
    }
}
