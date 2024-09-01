package transaction.route;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import transaction.dto.bot.Chat;
import transaction.dto.bot.User;
import transaction.handler.CallbackHandler;
import transaction.handler.MessageHandler;
import transaction.mapper.ChatMapper;
import transaction.service.database.UserSaveService;

@Service
@Slf4j
@RequiredArgsConstructor
public class Route {

    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;
    private final ChatMapper chatMapper;
    private final UserSaveService userSaveService;

    public void route(Update update) {
        Chat chat = createChat(update);

        if (chat.getUser() != null) {
            chat.setUser(saveUser(chat.getUser()));
        }

        if (chat.getHasMessage()) {
            messageHandler.handler(chat);
        } else if (chat.getHasCallback()) {
            callbackHandler.handler(chat);
        }
    }

    private Chat createChat(Update update) {
        log.debug("Update class: {}", update);
        Chat chat = chatMapper.chatMapper(update);
        log.debug("Chat class: {}", chat);
        return chat;
    }

    private User saveUser(User user) {
        if (user.getUserInfoHasSave() == null || !user.getUserInfoHasSave()) {
            return userSaveService.saveUser(user);
        } else {
            return user;
        }
    }
}