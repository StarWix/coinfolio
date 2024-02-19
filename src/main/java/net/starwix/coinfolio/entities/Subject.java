package net.starwix.coinfolio.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * A Subject could be either a Provider or an account.
 *
 * A Provider is identified by providerConfigSource only,
 * while an account is identified by providerConfigSource and providerConfigId (can be null).
 */
@Data
@Embeddable
public class Subject {
    @Nullable
    private String id; // must be unique
    private String providerConfigSource;
    @Nullable
    private String providerConfigId;
}
