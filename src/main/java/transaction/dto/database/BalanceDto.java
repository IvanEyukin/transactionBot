package transaction.dto.database;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "balance")
@IdClass(CompositeKeyBalance.class)
public class BalanceDto {
    @Id
    @Column(name = "user_id")
    long userId;
    @Id
    @Column(name = "account")
    int account;
    @Column(name = "balance")
    BigDecimal balance;
}