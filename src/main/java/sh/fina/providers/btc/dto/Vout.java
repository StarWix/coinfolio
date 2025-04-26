package sh.fina.providers.btc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Vout {

    private String scriptpubkey;

    @JsonProperty("scriptpubkey_asm")
    private String scriptpubkeyAsm;

    @JsonProperty("scriptpubkey_type")
    private String scriptpubkeyType;

    @JsonProperty("scriptpubkey_address")
    private String scriptpubkeyAddress;

    private long value;
}