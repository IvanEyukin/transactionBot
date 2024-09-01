package transaction.dto.bot;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private Boolean userInfoHasSave;
}