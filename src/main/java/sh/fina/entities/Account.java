package sh.fina.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private Type type;

    private BigDecimal amount;

    public enum Type {
        SPOT,
        FUTURES,
        MARGIN,
        DEPOSIT,
    }
}
