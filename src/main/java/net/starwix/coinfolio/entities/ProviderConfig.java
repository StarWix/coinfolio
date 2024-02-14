package net.starwix.coinfolio.entities;

import lombok.Data;
import java.util.Map;

@Data
public class ProviderConfig implements Subject {
    private int id;
    private String type;
    private String name;
    private Map<String, String> properties;
}
