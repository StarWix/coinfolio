package sh.fina.providers.btc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Vin {

    private String txid;

    private int vout;

    private Prevout prevout;

    private String scriptsig;

    @JsonProperty("scriptsig_asm")
    private String scriptsigAsm;

    @JsonProperty("is_coinbase")
    private boolean isCoinbase;

    private long sequence;
}
