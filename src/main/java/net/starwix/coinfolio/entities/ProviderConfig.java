package net.starwix.coinfolio.entities;

import jakarta.persistence.*;
import lombok.Data;
import net.starwix.coinfolio.models.ReadonlyProviderConfig;

import java.util.Map;

@Data
@Entity
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
