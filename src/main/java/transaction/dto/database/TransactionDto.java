package transaction.dto.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
public class TransactionDto {
    @Id
    @Column(name = "guid")
    UUID guid;
    @Column(name = "time_create", insertable = false, updatable = false)
    Timestamp timeCreate;
    @Column(name = "account")
    int account;
    @Column(name = "user_src")
    long userSrc;
    @Column(name = "user_dst")
    long userDst;
    @Column(name = "sum")
    BigDecimal sum;
    @Column(name = "comment")
    String comment;
}