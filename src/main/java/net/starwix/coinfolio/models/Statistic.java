package net.starwix.coinfolio.models;

import java.math.BigDecimal;
import java.time.Instant;

public record Statistic(Instant date, BigDecimal amount) {
}
