package net.starwix.coinfolio.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account implements Subject {
    private ProviderConfig providerConfig;
    private Asset asset;
    private BigDecimal amount;
    private AccountType type;
}
