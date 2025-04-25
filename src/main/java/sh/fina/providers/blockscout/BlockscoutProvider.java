package sh.fina.providers.blockscout;

import sh.fina.external.blockscount.client.ApiClient;
import sh.fina.external.blockscount.client.api.DefaultApi;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Provider;

import java.util.List;

public class BlockscoutProvider implements Provider {
    private final String source;
    private final DefaultApi api;

    public BlockscoutProvider(final String source, final String url) {
        this.source = source;
        final var apiClient = new ApiClient();
        apiClient.setBasePath(url);
        api = new DefaultApi(apiClient);
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public List<AbstractFetcher> createFetchers(ReadonlyProviderConfig config) {
        return List.of(
                new TransactionsFetcher(config, api),
                new IndirectTokenTransfersFetcher(config, api)
        );
    }
}
