package sh.fina.providers.eth;

import org.springframework.stereotype.Component;
import sh.fina.providers.blockscout.BlockscoutProvider;

@Component
public class EthProvider extends BlockscoutProvider {
    public EthProvider() {
        super("eth", "https://eth.blockscout.com/api/v2");
    }
}
