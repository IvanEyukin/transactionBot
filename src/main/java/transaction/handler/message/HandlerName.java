package transaction.handler.message;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import transaction.dto.bot.BotAnswer;
import transaction.dto.bot.Chat;
import transaction.dto.database.UserDto;
import transaction.mapper.UserMapper;
import transaction.sender.Sender;
import transaction.service.database.PostgresService;

@Component
@RequiredArgsConstructor
public class HandlerName {

    private final PostgresService postgresService;
    private final UserMapper userMapper;

    public BotAnswer answerName() {
        BotAnswer botAnswer = new BotAnswer();
        botAnswer.setMessage(Sender.USER_NAME_UPDATE);
        return botAnswer;
    }

    public Chat saveUserName(@NotNull Chat chat) {
        BotAnswer botAnswer = new BotAnswer();

        if (!chat.getMessage().getMessage().isBlank()) {
            String userName = chat.getMessage().getMessage();
            UserDto userInDatabase = postgresService.searchUser(userName);

            if (userInDatabase == null) {
                chat.getUser().setUserName(userName);
                UserDto userDto = userMapper.userDtoMapper(chat.getUser());
                postgresService.saveUser(userDto);
                botAnswer.setMessage(String.format(Sender.USER_NAME_SAVE, userName));
            } else {
                botAnswer.setMessage(Sender.USER_NAME_ERROR);
            }
        } else {
            botAnswer.setMessage(Sender.USER_NAME_INVALID);
        }

        chat.setBotAnswer(botAnswer);
        return chat;
    }
}