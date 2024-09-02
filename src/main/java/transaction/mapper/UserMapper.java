package transaction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import transaction.dto.bot.User;
import transaction.dto.database.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userInfoHasSave", ignore = true)
    User userMapper(Chat chat);

    @Mapping(target = "timeCreate", ignore = true)
    @Mapping(target = "isAdmin", ignore = true)
    UserDto userDtoMapper(User user);

    @Mapping(target = "userInfoHasSave", ignore = true)
    User userMapper(UserDto userDto);
}