package sh.fina.providers.blockscout;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import sh.fina.external.blockscount.client.api.DefaultApi;
import sh.fina.models.ReadonlyProviderConfig;

@Slf4j
public class TransactionsFetcher extends AbstractFetcher {
    public TransactionsFetcher(final ReadonlyProviderConfig config, final DefaultApi api) {
        super(config, api);
    }

    @Override
    public String getType() {
        return "transactions";
    }

    @Override
    public TransactionList<Meta> findTransactions(@Nullable final Meta meta) {
        final var nextPageParams = meta == null ? null : meta.getNextPageParams();
        final var blockchainTransactions = api.getAddressTxs(publicKey, null, nextPageParams);

        final var transactions = blockchainTransactions.getItems().stream()
                .map(this::convert)
                .toList();

        return new TransactionList<>(
                transactions,
                blockchainTransactions.getNextPageParams() == null ? null : new Meta(blockchainTransactions.getNextPageParams())
        );
    }
}
