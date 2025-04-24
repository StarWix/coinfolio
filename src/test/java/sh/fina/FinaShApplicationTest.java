package sh.fina;

import sh.fina.entities.ProviderConfig;
import sh.fina.models.Statistic;
import sh.fina.repositories.ProviderConfigRepository;
import sh.fina.services.PortfolioService;
import sh.fina.services.PriceService;
import sh.fina.services.TransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FinaShApplicationTest {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private ProviderConfigRepository providerConfigRepository;

    private static final BigDecimal DELTA = new BigDecimal("0.001");

    @BeforeAll
    public static void cleanup() throws IOException {
        final var db = Path.of("mydatabase.db");
        if (Files.exists(db)) {
            Files.delete(db);
        }
    }

    @Test
    void test() {
        providerConfigRepository.save(ProviderConfig.builder()
                .source("dummy")
                .name("first")
                .properties(Map.of("direction", "ASC"))
                .build());
        providerConfigRepository.save(ProviderConfig.builder()
                .source("dummy")
                .name("second")
                .properties(Map.of("direction", "DESC"))
                .build());

        transactionService.pull();
        priceService.pull("USD", ChronoUnit.DAYS);
        final var statistic = portfolioService.overview("USD", ChronoUnit.DAYS);
        System.out.println(statistic);
        assertOnDate(statistic, "2025-01-01T00:00:00Z", "19954.3751778"); // first dummy transaction
        assertOnDate(statistic, "2025-01-02T00:00:00Z", "20517.9806936"); // second dummy transaction
        assertOnDate(statistic, "2025-02-01T00:00:00Z", "21320.2785657"); // third dummy transaction
    }

    void assertOnDate(List<Statistic> statistic, String dateText, String expectedAmount) {
        final var date = Instant.parse(dateText);
        var statisticOnDate = statistic.stream()
                .filter(x -> x.date().equals(date))
                .toList();
        assertEquals(1, statisticOnDate.size());
        var expected = new BigDecimal(expectedAmount);
        var relativeError = expected.divide(statisticOnDate.getFirst().amount(), RoundingMode.HALF_DOWN)
                .subtract(BigDecimal.ONE)
                .abs();
        assertTrue(relativeError.compareTo(DELTA) < 0, relativeError.toString());
    }
}