package net.starwix.coinfolio.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Map;

@Data
@Entity
public class ProviderConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String source;
    private String name;

    @ElementCollection
    @CollectionTable(name = "provider_config_properties", joinColumns = @JoinColumn(name = "provider_config_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> properties;
}
