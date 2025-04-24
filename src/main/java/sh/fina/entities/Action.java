package sh.fina.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import sh.fina.services.TransactionService;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Set by {@link TransactionService}
     */
    @ManyToOne
    private Transaction transaction;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="providerConfigSource", column=@Column(name="sender_provider_config_source")),
            @AttributeOverride(name="accountId", column=@Column(name="sender_account_id")),
            @AttributeOverride(name="providerConfigId", column=@Column(name="sender_provider_config_id"))
    })
    private Subject sender;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="providerConfigSource", column=@Column(name="recipient_provider_config_source")),
            @AttributeOverride(name="accountId", column=@Column(name="recipient_account_id")),
            @AttributeOverride(name="providerConfigId", column=@Column(name="recipient_provider_config_id"))
    })
    private Subject recipient;

    private String assetSymbol;

    /**
     * Must be positive.
     */
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Set by {@link TransactionService}
     */
    @Enumerated(EnumType.STRING)
    private Direction direction;

    public BigDecimal getPortfolioAmount() {
        return direction.getPortfolioAmount(amount);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Action action = (Action) o;
        return Objects.equals(transaction.getId(), action.transaction.getId())
                && Objects.equals(sender, action.sender)
                && Objects.equals(recipient, action.recipient)
                && Objects.equals(assetSymbol, action.assetSymbol)
                && type == action.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction.getId(), sender, recipient, assetSymbol, type);
    }

    public enum Type {
        TRANSFER,
        FEE
    }

    public enum Direction {
        SENDER,
        RECIPIENT,
        BOTH,
        NONE;

        public static Direction resolve(final boolean isSender, final boolean isRecipient) {
            if (isSender) {
                if (isRecipient) {
                    return Direction.BOTH;
                }
                return SENDER;
            }
            if (isRecipient) {
                return Direction.RECIPIENT;
            }
            return Direction.NONE;
        }

        private BigDecimal getPortfolioAmount(final BigDecimal amount) {
            return switch (this) {
                case SENDER -> amount.negate();
                case RECIPIENT -> amount;
                case BOTH, NONE -> BigDecimal.ZERO;
            };
        }
    }
}
