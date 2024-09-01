package transaction.dto.database;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserDto {
    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "is_admin", insertable = false, updatable = false)
    private Boolean isAdmin;
}