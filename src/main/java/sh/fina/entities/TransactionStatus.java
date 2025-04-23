package sh.fina.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionStatus {
    COMPLETED(true),
    PROCESSING(false),
    CANCELED(true),
    ERROR(true);

    private final boolean terminated;
}
