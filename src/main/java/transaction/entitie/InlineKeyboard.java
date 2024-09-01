package transaction.entitie;

import lombok.Data;

@Data
public class InlineKeyboard {
    private String message;
    private String callBack;

    public InlineKeyboard(String message, String callBack) {
        this.message = message;
        this.callBack = callBack;
    }
}