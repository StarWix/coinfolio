package sh.fina.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import sh.fina.services.TransactionService;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "\"transaction\"")
@SuperBuilder
@NoArgsConstructor
public class Transaction {
    @EmbeddedId
    private Id id;

    /**
     * Set by {@link TransactionService}
     */
    private String fetcherType;

    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaction", orphanRemoval = true)
    private List<Action> actions;

    private String note;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="providerSource", column=@Column(name="child_provider_source")),
            @AttributeOverride(name="transactionId", column=@Column(name="child_transaction_id")),
            @AttributeOverride(name="providerConfigId", column=@Column(name="child_provider_config_id"))
    })
    private Id childId;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable {
        private String providerSource;
        private String transactionId;
        /**
         * May be nullable only for childId.
         */
        @Nullable
        private Integer providerConfigId;

        public Id(final String providerSource, final String transactionId) {
            this.providerSource = providerSource;
            this.transactionId = transactionId;
        }
    }

    @AllArgsConstructor
    @Getter
    public enum Status {
        COMPLETED(true),
        PROCESSING(false),
        CANCELED(true),
        ERROR(true);

        private final boolean terminated;
    }
}
