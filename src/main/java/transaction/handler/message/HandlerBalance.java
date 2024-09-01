package transaction.handler.message;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import transaction.dto.bot.BotAnswer;
import transaction.dto.bot.Chat;
import transaction.dto.database.AccountDto;
import transaction.dto.database.BalanceDto;
import transaction.sender.Sender;
import transaction.service.database.PostgresService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HandlerBalance {

    private final PostgresService postgresService;

    public BotAnswer getBalanceList(@NotNull Chat chat) {
        List<BalanceDto> balanceDtoList = postgresService.getBalanceList(chat.getUser().getId());
        Map<Integer, AccountDto> accountDtoMap = getAccountDtoMap(postgresService.getAccountList());
        String message = setListToMessage(chat.getUser().getUserName(), balanceDtoList, accountDtoMap);

        BotAnswer botAnswer = new BotAnswer();
        botAnswer.setMessage(message);
        return botAnswer;
    }

    public String getBalance(long userId, int account) {
        BalanceDto balanceDto = postgresService.getBalance(userId, account);
        AccountDto accountDto = postgresService.getAccount(account);
        return String.format(Sender.BALANCE_ACCOUNT, accountDto.getTranslate(), balanceDto.getBalance());
    }

    private String setListToMessage(String userName, @NotNull List<BalanceDto> balanceDtoList, Map<Integer, AccountDto> accountDtoMap) {
        String message = String.format(Sender.BALANCE, userName);

        for (BalanceDto balanceDto : balanceDtoList) {
            String account = String.format("\n%s: %s", accountDtoMap.get(balanceDto.getAccount()).getTranslate(), balanceDto.getBalance());
            message = message.concat(account);
        }
        return message;
    }

    @NotNull
    private Map<Integer, AccountDto> getAccountDtoMap(@NotNull List<AccountDto> accountDtoList) {
        Map<Integer, AccountDto> accountDtoMap = new HashMap<>();
        for (AccountDto accountDto : accountDtoList) {
            accountDtoMap.put(accountDto.getId(), accountDto);
        }
        return accountDtoMap;
    }
}