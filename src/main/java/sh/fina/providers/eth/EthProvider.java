package sh.fina.providers.eth;

import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EthProvider implements Provider {
    @Override
    public String getSource() {
        return "eth";
    }

    @Override
    public List<EthFetcher> createFetchers(ReadonlyProviderConfig config) {
        return List.of(
                new TransactionsFetcher(config),
                new IndirectTokenTransfersFetcher(config)
        );
    }
}
