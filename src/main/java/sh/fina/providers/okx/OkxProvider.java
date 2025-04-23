package sh.fina.providers.okx;

import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;
import sh.fina.providers.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OkxProvider implements Provider {
    @Override
    public String getSource() {
        return "okx";
    }

    @Override
    public List<? extends Fetcher<?>> createFetchers(ReadonlyProviderConfig config) {
        return List.of(
            // https://github.com/knowm/XChange/issues/5027
        );
    }
}
