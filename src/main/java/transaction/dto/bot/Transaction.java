package transaction.dto.bot;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Transaction {
    private UUID guid;
    private int account;
    private long userSrc;
    private long userDst;
    private BigDecimal sum;
}