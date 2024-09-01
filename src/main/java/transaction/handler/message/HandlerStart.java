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
public class HandlerStart {

    private final PostgresService postgresService;
    private final UserMapper userMapper;

    public BotAnswer execute(@NotNull Chat chat) {
        BotAnswer botAnswer = new BotAnswer();

        UserDto userDto = postgresService.getUser(userMapper.userDtoMapper(chat.getUser()));
        if (userDto.getUserName() == null) {
            botAnswer.setMessage(Sender.START_NO_USER);
        } else {
            botAnswer.setMessage(String.format(Sender.START, chat.getUser().getUserName()));
        }
        return botAnswer;
    }
}