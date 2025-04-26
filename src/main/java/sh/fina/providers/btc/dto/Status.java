package sh.fina.providers.btc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Status {

    private boolean confirmed;

    @JsonProperty("block_height")
    private int blockHeight;

    @JsonProperty("block_hash")
    private String blockHash;

    @JsonProperty("block_time")
    private long blockTime;
}