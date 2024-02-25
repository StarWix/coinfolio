package net.starwix.coinfolio.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

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
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private ActionType type;
}
