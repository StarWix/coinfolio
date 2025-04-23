package sh.fina.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import sh.fina.models.ReadonlyProviderConfig;

import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@SuperBuilder
public class ProviderConfig implements ReadonlyProviderConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String source;
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_config_properties", joinColumns = @JoinColumn(name = "provider_config_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> properties;
}
