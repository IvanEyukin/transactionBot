package transaction.dto.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")
public class AccountDto {
    @Id
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "translate")
    String translate;
}