package transaction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import transaction.dto.bot.Callback;

@Mapper(componentModel = "spring")
public interface CallbackMapper {
    @Mapping(target = "callbackData", source = "data")
    Callback callbackMapper(CallbackQuery callbackQuery);
}