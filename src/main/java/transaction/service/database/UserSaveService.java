package transaction.service.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import transaction.dto.bot.User;
import transaction.dto.database.UserDto;
import transaction.mapper.UserMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSaveService {

    private final UserMapper userMapper;
    private final PostgresService postgresService;

    public User saveUser(User user) {
        UserDto userDto = userMapper.userDtoMapper(user);
        UserDto userDb = postgresService.getUser(userDto);

        if (userDb == null) {
            postgresService.saveUser(userDto);
            user.setUserInfoHasSave(true);
            return user;
        } else {
            if (userDb.getFirstName() != null && !userDb.getFirstName().equals(user.getFirstName()) || userDb.getLastName() != null && !userDb.getLastName().equals(user.getLastName())) {
                postgresService.saveUser(userDto);
            }
            user.setUserInfoHasSave(true);
            return userMapper.userMapper(userDb);
        }
    }
}