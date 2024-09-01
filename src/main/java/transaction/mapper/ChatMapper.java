package transaction.mapper;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.telegram.telegrambots.meta.api.objects.Update;
import transaction.dto.bot.Chat;

@Mapper(componentModel = "spring", uses = {UserMapper.class, MessageMapper.class, CallbackMapper.class})
public interface ChatMapper {
    @Mapping(target = "id", qualifiedByName = "mapId", source = "update")
    @Mapping(target = "hasMessage", qualifiedByName = "mapHasMessage", source = "update")
    @Mapping(target = "hasCallback", qualifiedByName = "mapHasCallback", source = "update")
    @Mapping(target = "user", source = "update.message.chat")
    @Mapping(target = "message", source = "update.message")
    @Mapping(target = "callback", source = "update.callbackQuery")
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "botAnswer", ignore = true)
    Chat chatMapper(Update update);

    @Mapping(target = "id", source = "chatRepository.id")
    @Mapping(target = "hasMessage", source = "chat.hasMessage")
    @Mapping(target = "hasCallback", source = "chat.hasCallback")
    @Mapping(target = "user", source = "chatRepository.user")
    @Mapping(target = "message", source = "chat.message")
    @Mapping(target = "callback", source = "chat.callback")
    @Mapping(target = "transaction", source = "chatRepository.transaction")
    @Mapping(target = "state", source = "chatRepository.state")
    @Mapping(target = "botAnswer", source = "chatRepository.botAnswer")
    Chat chatRepositoryMapper(Chat chat, Chat chatRepository);

    @Named("mapId")
    default Long mapId(@NotNull Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        } else {
            return update.getMessage().getChatId();
        }
    }

    @Named("mapHasMessage")
    default Boolean mapHasMessage(@NotNull Update update) {
        return (update.hasMessage() && update.getMessage().hasText());
    }

    @Named("mapHasCallback")
    default Boolean mapHasCallback(@NotNull Update update) {
        return update.hasCallbackQuery();
    }
}