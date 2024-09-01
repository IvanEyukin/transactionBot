package transaction.dto.database;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users_transactions")
@IdClass(CompositeKeyUserTransaction.class)
public class UserTransactionView {
    @Id
    @Column(name = "user_src")
    private long userSrc;
    @Id
    @Column(name = "id")
    private long userDst;
    @Column(name = "user_name")
    private String userName;
}