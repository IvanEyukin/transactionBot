package transaction.dto.database;

import lombok.Data;

@Data
public class CompositeKeyBalance {
    private long userId;
    private int account;
}