package transaction.dto.database;

import lombok.Data;

@Data
public class CompositeKeyUserTransaction {
    private long userSrc;
    private long userDst;
}