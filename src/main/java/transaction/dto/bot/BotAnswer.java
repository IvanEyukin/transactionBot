package transaction.dto.bot;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Objects;

@Data
public class BotAnswer {
    private Integer id;
    private String message;
    private Boolean messageHasKeyboard;
    private InlineKeyboardMarkup keyboard;

    public Boolean getMessageHasKeyboard() {
        return Objects.requireNonNullElseGet(messageHasKeyboard, () -> keyboard != null);
    }
}