package net.starwix.coinfolio.models;

import java.util.Map;

public interface ReadonlyProviderConfig {
    int getId();
    String getSource();
    String getName();
    Map<String, String> getProperties();
}
