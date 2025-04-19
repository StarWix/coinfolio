package net.starwix.coinfolio.models;

import java.util.Map;

public interface ReadonlyProviderConfig {
    int getId();
    String getSource();
    String getName();
    Map<String, String> getProperties();

    default String getProperty(String key) {
        final String property = getProperties().get(key);
        if (property == null) {
            throw new IllegalArgumentException("Property '" + key + "' not found");
        }
        return property;
    }
}
