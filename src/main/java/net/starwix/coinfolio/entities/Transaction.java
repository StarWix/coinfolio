package net.starwix.coinfolio.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "\"transaction\"")
public class Transaction {
    @EmbeddedId
    private Id id;

    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaction")
    private List<Action> actions;

    private String note;

    private String childProviderConfigSource;
    private String childId;
    @Nullable
    private Integer childProviderConfigId;

    @Data
    @Embeddable
    public static class Id implements Serializable {
        private int providerConfigId;
        private String id;
    }
}
