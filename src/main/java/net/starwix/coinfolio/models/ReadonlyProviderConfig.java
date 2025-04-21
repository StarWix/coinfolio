package net.starwix.coinfolio.models;

import java.util.Map;

public interface ReadonlyProviderConfig {
    String API_KEY = "apiKey";
    String SECRET_KEY = "apiKey";
    String ADDRESS = "address";

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

    default String getApiKey() {
        return getProperty(API_KEY);
    }

    default String getSecretKey() {
        return getProperty(SECRET_KEY);
    }

    default String getAddress() {
        return getProperty(ADDRESS);
    }
}
