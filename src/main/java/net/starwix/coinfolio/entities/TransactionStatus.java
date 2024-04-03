package net.starwix.coinfolio.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionStatus {
    COMPLETED(true),
    PROCESSING(false),
    CANCELED(true);

    private final boolean terminated;
}
