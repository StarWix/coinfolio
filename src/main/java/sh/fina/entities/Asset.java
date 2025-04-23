package sh.fina.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Asset {
    @Id
    private String symbol;
}
