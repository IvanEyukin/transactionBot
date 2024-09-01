package transaction.handler.message;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import transaction.dto.bot.BotAnswer;
import transaction.dto.bot.Chat;
import transaction.dto.database.AccountDto;
import transaction.dto.database.UserDto;
import transaction.entitie.InlineKeyboard;
import transaction.sender.Sender;
import transaction.service.KeyboardMessage;
import transaction.service.database.PostgresService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HandlerAdmin {

    private final PostgresService postgresService;
    private final KeyboardMessage keyboardMessage;

    public Boolean checkUserIsAdmin(@NotNull Chat chat) {
        UserDto userDto = postgresService.getUser(chat.getUser().getId());
        return userDto.getIsAdmin();
    }

    public BotAnswer menu() {
        BotAnswer botAnswer = new BotAnswer();
        botAnswer.setMessage(Sender.ADMIN_ANSWER);

        List<InlineKeyboard> inlineKeyboardList = List.of(
                new InlineKeyboard("Список пользователей", "UserList"),
                new InlineKeyboard("Обновить баланс пользователя", "UpdateBalanceUser"),
                new InlineKeyboard("Обновить баланс всех пользователей", "UpdateBalanceAll")
        );

        botAnswer.setKeyboard(keyboardMessage.createinlineKeyboardMarkup(inlineKeyboardList));
        return botAnswer;
    }

    public BotAnswer getUserList() {
        List<UserDto> userDtoList = postgresService.getUserList();
        String message = Sender.ADMIN_USERS;

        for (UserDto userDto : userDtoList) {
            message = message.concat(String.format("\n%s %s %s", userDto.getFirstName(), userDto.getLastName(), userDto.getUserName()));
        }

        BotAnswer botAnswer = new BotAnswer();
        botAnswer.setMessage(message);

        return botAnswer;
    }

    public BotAnswer answerUser() {
        BotAnswer botAnswer = new BotAnswer();
        botAnswer.setMessage(Sender.ADMIN_ANSWER_USER);
        return botAnswer;
    }

    public BotAnswer answerUserAccount() {
        BotAnswer botAnswer = new BotAnswer();

        List<AccountDto> accountDtoList = postgresService.getAccountList();

        List<InlineKeyboard> inlineKeyboardList = new ArrayList<>();
        for (AccountDto accountDto : accountDtoList) {
            inlineKeyboardList.add(new InlineKeyboard(accountDto.getTranslate(), String.valueOf(accountDto.getId())));
        }
        inlineKeyboardList.add(new InlineKeyboard("Все", "All"));

        botAnswer.setMessage(Sender.ADMIN_ANSWER_ACCOUNT);
        botAnswer.setKeyboard(keyboardMessage.createinlineKeyboardMarkup(inlineKeyboardList));

        return botAnswer;
    }

    public BotAnswer answerBalance() {
        BotAnswer botAnswer = new BotAnswer();
        botAnswer.setMessage(Sender.ADMIN_ANSWER_BALANCE);
        return botAnswer;
    }

    public void updateBalance(@NotNull Chat chat) {
        BigDecimal sum = new BigDecimal(chat.getMessage().getMessage());
        if (chat.getTransaction() != null) {
            postgresService.updateUserBalance(chat.getTransaction().getUserDst(),chat.getTransaction().getAccount(), sum);
        } else {
            postgresService.updateBalance(sum);
        }
    }
}