package sh.fina.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@IdClass(Price.Id.class)
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @jakarta.persistence.Id
    private String currencySymbol;
    @jakarta.persistence.Id
    private ChronoUnit timeframe;
    @jakarta.persistence.Id
    private String assetSymbol;
    @jakarta.persistence.Id
    private Instant openTimestamp;
    private BigDecimal closePrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable {
        private String assetSymbol;
        private ChronoUnit timeframe;
        private String currencySymbol;
        private Instant openTimestamp;
    }
}
