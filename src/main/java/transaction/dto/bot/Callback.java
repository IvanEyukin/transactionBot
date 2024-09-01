package transaction.dto.bot;

import lombok.Data;

@Data
public class Callback {
    private String id;
    private String callbackData;
}