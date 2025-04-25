package sh.fina.providers.arb;

import org.springframework.stereotype.Component;
import sh.fina.providers.blockscout.BlockscoutProvider;

@Component
public class ArbProvider extends BlockscoutProvider {
    public ArbProvider() {
        super("arb", "https://arbitrum.blockscout.com/api/v2");
    }
}
