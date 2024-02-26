package net.starwix.coinfolio.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@IdClass(Price.Id.class)
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @jakarta.persistence.Id
    private String assetSymbol;
    @jakarta.persistence.Id
    private Instant date;
    @jakarta.persistence.Id
    private String currencySymbol;
    private BigDecimal price;

    @Data
    public static class Id implements Serializable {
        private String assetSymbol;
        private Instant date;
        private String currencySymbol;
    }
}
