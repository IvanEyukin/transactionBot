package transaction.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import transaction.entitie.InlineKeyboard;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardMessage {

    private InlineKeyboardButton createInlineKeyboardButton(InlineKeyboard inlineKeyboard) {
        return InlineKeyboardButton
                .builder()
                .text(inlineKeyboard.getMessage())
                .callbackData(inlineKeyboard.getCallBack())
                .build();
    }

    public InlineKeyboardMarkup createinlineKeyboardMarkup(InlineKeyboard inlineKeyboard) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        InlineKeyboardRow row = new InlineKeyboardRow();

        row.add(createInlineKeyboardButton(inlineKeyboard));
        keyboard.add(row);

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup createinlineKeyboardMarkup(List<InlineKeyboard> inlineKeyboardList) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        for (InlineKeyboard inlineKeyboard : inlineKeyboardList) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(createInlineKeyboardButton(inlineKeyboard));
            keyboard.add(row);
        }
        return new InlineKeyboardMarkup(keyboard);
    }
}