package transaction.dto.bot;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
public class BotAnswer {
    private Integer id;
    private String message;
    private Boolean messageHasKeyboard;
    private InlineKeyboardMarkup keyboard;
}