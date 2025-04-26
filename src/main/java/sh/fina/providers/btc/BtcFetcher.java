package sh.fina.providers.btc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sh.fina.entities.*;
import sh.fina.models.ReadonlyProviderConfig;
import sh.fina.providers.Fetcher;
import sh.fina.providers.btc.dto.Status;
import sh.fina.providers.btc.dto.TransactionDto;
import sh.fina.providers.btc.dto.Vin;
import sh.fina.providers.btc.dto.Vout;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BtcFetcher implements Fetcher<BtcFetcher.Meta> {
    private final RestClient restClient;
    private final int id;
    private final String source;
    private final String addressPublicKey;

    private static final String PROVIDER_URL = "https://blockstream.info/api/address/%s/txs";
    private static final String BTC_SYMBOL = "BTC";
    private static final int SATOSHI_TO_BTC_POWER_DIVIDER = 8;

    public BtcFetcher(final ReadonlyProviderConfig config, RestClient restClient) {
        this.restClient = restClient;
        this.addressPublicKey = config.getProperties().get("publicKey");
        id = config.getId();
        source = config.getSource();
    }

    @Override
    public int getProviderConfigId() {
        return id;
    }

    @Override
    public String getType() {
        return "btc";
    }

    @Override
    public List<Account> findAccounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Direction getDirection() {
        return Direction.DESC;
    }

    @Override
    public Class<Meta> getMetaClass() {
        return Meta.class;
    }

    @Override
    public boolean owns(Subject subject) {
        return addressPublicKey.equals(subject.getAccountId());
    }

    @Override
    public TransactionList<Meta> findTransactions(@Nullable Meta meta) {

        String url = String.format(PROVIDER_URL, addressPublicKey);

        if (meta != null && meta.getLastTransactionId() != null) {
            url += "/chain/" + meta.getLastTransactionId();
        }

        List<TransactionDto> transactions = restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (transactions == null || transactions.isEmpty()) {
            return new TransactionList<>(Collections.emptyList(), null);
        } else {
            return new TransactionList<>(transactions.stream()
                    .map(this::convert)
                    .toList(), new Meta(transactions.getLast().getTxid()));
        }
    }

    private Transaction convert(TransactionDto transaction) {
        List<Action> actions = Stream.concat(
                        transaction.getVin().stream().map(this::convert),
                        transaction.getVout().stream().map(this::convert))
                .filter(Objects::nonNull)
                .toList();

        return Transaction.builder()
                .id(new Transaction.Id(source, transaction.getTxid(), id))
                .createdAt(Instant.ofEpochSecond(transaction.getStatus().getBlockTime()))
                .status(convert(transaction.getStatus()))
//                .note() // todo what is note ?
                .actions(actions)
                .build();
    }

    private Action convert(Vin vin) {
        if (!vin.getPrevout().getScriptpubkeyAddress().equals(addressPublicKey)) {
            return null;
        }
        return Action.builder()
                .type(Action.Type.TRANSFER)
                .amount(BigDecimal.valueOf(vin.getPrevout().getValue())
                        .scaleByPowerOfTen(SATOSHI_TO_BTC_POWER_DIVIDER))
                .assetSymbol(BTC_SYMBOL)
                .build();
    }

    private Action convert(Vout vout) {
        if (!vout.getScriptpubkeyAddress().equals(addressPublicKey)) {
            return null;
        }

        return Action.builder()
                .type(Action.Type.TRANSFER)
                .amount(BigDecimal.valueOf(vout.getValue())
                        .scaleByPowerOfTen(-SATOSHI_TO_BTC_POWER_DIVIDER))
                .assetSymbol(BTC_SYMBOL)
                .build();
    }

    private Transaction.Status convert(final Status status) {
        return status.isConfirmed() ? Transaction.Status.COMPLETED : Transaction.Status.PROCESSING;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private String lastTransactionId;
    }
}
