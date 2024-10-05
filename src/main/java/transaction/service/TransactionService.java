package transaction.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import transaction.dto.bot.Chat;
import transaction.dto.bot.Transaction;
import transaction.dto.bot.User;
import transaction.dto.database.UserDto;
import transaction.entitie.NumberInvalid;
import transaction.mapper.UserMapper;
import transaction.service.database.PostgresService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final PostgresService postgresService;
    private final UserMapper userMapper;

    private UserDto getUserDst(long userId) {
        return postgresService.getUser(userId);
    }

    private UserDto getUserDst(String userId) {
        return postgresService.searchUser(userId);
    }

    @NotNull
    private Boolean checkMessageHasNumber(String message) {
        return NumberUtils.isCreatable(message);
    }

    @NotNull
    @Contract(pure = true)
    private Boolean checkMessageHasPoint(@NotNull String message) {
        return message.contains(".");
    }

    @NotNull
    private Boolean checkNumberIsPositive(@NotNull BigDecimal sum) {
        return sum.compareTo(new BigDecimal(0)) >= 1;
    }

    @NotNull
    private Boolean checkBalanceIsSufficient(@NotNull BigDecimal balance, BigDecimal sum) {
        return balance.compareTo(sum) >= 0;
    }

    public User getUserDst(@NotNull Chat chat) {
        UserDto userDto;
        if (chat.getHasMessage()) {
            userDto = getUserDst(chat.getMessage().getMessage());
        } else {
            userDto = getUserDst(Long.parseLong(chat.getCallback().getCallbackData()));
        }

        if (userDto != null) {
            return userMapper.userMapper(userDto);
        } else {
            return null;
        }
    }

    public Transaction create(long userSrcId, long userDstId) {
        Transaction transaction = new Transaction();
        transaction.setGuid(UUID.randomUUID());
        transaction.setUserSrc(userSrcId);
        transaction.setUserDst(userDstId);
        return transaction;
    }

    public NumberInvalid checkNumber(String message, BigDecimal balance) {
        if (checkMessageHasNumber(message) && !checkMessageHasPoint(message)) {
            BigDecimal sum = new BigDecimal(message);
            if (checkNumberIsPositive(sum)) {
                if (checkBalanceIsSufficient(balance, sum)) {
                    return NumberInvalid.VALID;
                } else {
                    return NumberInvalid.DEFICIENCY;
                }
            } else {
                return NumberInvalid.ZERO;
            }
        } else {
            return NumberInvalid.INVALID;
        }
    }
}