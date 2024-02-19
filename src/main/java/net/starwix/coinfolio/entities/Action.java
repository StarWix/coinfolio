package net.starwix.coinfolio.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Action {
    @Id
    private Long id;

    @ManyToOne
    private Transaction transaction;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="id", column=@Column(name="sender_id", unique = true)),
            @AttributeOverride(name="providerConfigSource", column=@Column(name="sender_providerConfigSource")),
            @AttributeOverride(name="providerConfigId", column=@Column(name="sender_providerConfigId"))
    })
    private Subject sender;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="id", column=@Column(name="recipient_id", unique = true)),
            @AttributeOverride(name="providerConfigSource", column=@Column(name="recipient_providerConfigSource")),
            @AttributeOverride(name="providerConfigId", column=@Column(name="recipient_providerConfigId"))
    })
    private Subject recipient;

    private String assetSymbol;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private ActionType type;
}
