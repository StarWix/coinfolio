package net.starwix.coinfolio.entities;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

@Data
public class Transaction {
    private Id id;
    private Instant createdAt;
    private TransactionStatus status;
    private List<Action> actions;
    @Nullable
    private String note;
    @Nullable
    private Id childId;

    private static class Id {
        private ProviderConfig providerConfig;
        private String id;
    }
}
