package net.starwix.coinfolio.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@IdClass(FetcherState.Id.class)
@NoArgsConstructor
@AllArgsConstructor
public class FetcherState {
    @jakarta.persistence.Id
    private int providerConfigId;
    @jakarta.persistence.Id
    private String fetcherType;
    private String meta;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable {
        private int providerConfigId;
        private String fetcherType;
    }
}
