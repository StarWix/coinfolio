package net.starwix.coinfolio.entities;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    /**
     * Must be unique for provider source.
     */
    private String id;
    private String providerConfigSource;
    @Nullable
    private String providerConfigId;
    private String assetSymbol;
    private BigDecimal amount;
    private AccountType type;
}
