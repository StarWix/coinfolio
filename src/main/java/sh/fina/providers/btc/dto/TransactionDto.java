package sh.fina.providers.btc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionDto {

    private String txid;

    private int version;

    private long locktime;

    private List<Vin> vin;

    private List<Vout> vout;

    private int size;

    private int weight;

    private long fee;

    private Status status;
}