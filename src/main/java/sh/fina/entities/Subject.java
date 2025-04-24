package sh.fina.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import sh.fina.services.TransactionService;

import java.io.Serializable;

/**
 * A Subject could be either a Provider or an account.
 *
 * A Provider is identified by providerConfigSource only,
 * while an account is identified by providerConfigSource, accountId and providerConfigId (can be null).
 */
@Data
@Embeddable
@NoArgsConstructor
public class Subject implements Serializable {
    private String providerConfigSource;

    /**
     * Must be unique for providerConfigSource.
     */
    @Nullable
    private String accountId;

    /**
     * Set by {@link TransactionService}
     */
    @Nullable
    private Integer providerConfigId;

    public Subject(final String providerConfigSource) {
        this.providerConfigSource = providerConfigSource;
    }

    public Subject(final String providerConfigSource, final String accountId) {
        this.providerConfigSource = providerConfigSource;
        this.accountId = accountId;
    }
}
