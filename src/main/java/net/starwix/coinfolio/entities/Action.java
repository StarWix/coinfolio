package net.starwix.coinfolio.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    @ManyToOne
    private Transaction transaction;

    private String accountId;

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
     * Must be positive if recipient equals transaction.accountId. Otherwise negative
     */
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private ActionType type;

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
}
