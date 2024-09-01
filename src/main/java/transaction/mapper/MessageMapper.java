package transaction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import transaction.dto.bot.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id", source = "message.messageId")
    @Mapping(target = "message", source = "message.text")
    Message messageMapper(org.telegram.telegrambots.meta.api.objects.message.Message message);
}