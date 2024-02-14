package net.starwix.coinfolio.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Action {
    private Subject sender;
    private Subject recipient;
    private Asset asset;
    private BigDecimal amount;
    private ActionType type;
}
