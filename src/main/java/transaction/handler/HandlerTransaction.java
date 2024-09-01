package transaction.handler;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import transaction.dto.bot.BotAnswer;
import transaction.dto.bot.Chat;
import transaction.dto.bot.Transaction;
import transaction.dto.bot.User;
import transaction.dto.database.*;
import transaction.entitie.InlineKeyboard;
import transaction.mapper.TransactionMapper;
import transaction.sender.Sender;
import transaction.service.KeyboardMessage;
import transaction.service.TransactionService;
import transaction.service.database.PostgresService;
import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
public class HandlerTransaction {

    private final PostgresService postgresService;
    private final KeyboardMessage keyboardMessage;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public BotAnswer startTransaction(@NotNull Chat chat) {
        return answerUserDst(chat.getUser().getId(), Sender.TRANSACTION_USER);
    }

    public Chat setUserDst(@NotNull Chat chat) {
        User userDst = transactionService.getUserDst(chat);

        if (userDst == null) {
            chat.setBotAnswer(answerUserDst(chat.getUser().getId(), Sender.TRANSACTION_USER_ERROR));
        } else {
            chat.setTransaction(transactionService.create(chat.getUser().getId(), userDst.getId()));
            chat.setBotAnswer(answerAccount(chat.getUser().getId()));
        }

        return chat;
    }

    public Chat setAccount(@NotNull Chat chat) {
        BotAnswer botAnswer = new BotAnswer();
        chat.getTransaction().setAccount(Integer.parseInt(chat.getCallback().getCallbackData()));

        botAnswer.setMessage(Sender.TRANSACTION_SUM);
        chat.setBotAnswer(botAnswer);

        return chat;
    }

    public Chat setBalance(@NotNull Chat chat) {
        BotAnswer botAnswer = new BotAnswer();
        BalanceDto balanceDto = postgresService.getBalance(chat.getUser().getId(), chat.getTransaction().getAccount());

        switch (transactionService.checkNumber(chat.getMessage().getMessage(), balanceDto.getBalance())){
            case INVALID -> botAnswer.setMessage(Sender.TRANSACTION_SUM_ERROR_INT);
            case DEFICIENCY -> botAnswer.setMessage(Sender.TRANSACTION_SUM_ERROR_DEFICIENCY);
            case ZERO -> botAnswer.setMessage(Sender.TRANSACTION_SUM_ERROR_ZERO);
            case VALID -> {
                chat.getTransaction().setSum(new BigDecimal(chat.getMessage().getMessage()));
                UserDto userDto = postgresService.getUser(chat.getTransaction().getUserDst());
                AccountDto accountDto = postgresService.getAccount(chat.getTransaction().getAccount());
                botAnswer = answerConfirm(userDto.getUserName(), accountDto.getTranslate(), chat.getTransaction().getSum());
            }
        }

        chat.setBotAnswer(botAnswer);
        return chat;
    }

    public void saveTransaction(@NotNull Chat chat) {
        postgresService.saveTransaction(transactionMapper.transactionMapper(chat.getTransaction()));
    }

    public Transaction adminCancellation(@NotNull Chat chat) {
        UUID transactionGuid = UUID.fromString(chat.getCallback().getCallbackData().replace("CancelAdminTransaction.", ""));
        TransactionDto transactionDto = postgresService.getTransaction(transactionGuid);
        postgresService.saveTransaction(transactionMapper.transactionNewMapper(transactionDto));
        return transactionMapper.transactionDtoMapper(transactionDto);
    }

    @NotNull
    private BotAnswer answerUserDst(long userSrcId, String message) {
        List<UserTransactionView> userTransactionViewList = postgresService.getUserTransactionList(userSrcId);

        BotAnswer botAnswer = new BotAnswer();
        botAnswer.setMessage(message);

        if (userTransactionViewList != null) {
            botAnswer.setKeyboard(keyboardMessage.createinlineKeyboardMarkup(getKeyboard(userTransactionViewList)));
        }

        return botAnswer;
    }

    @NotNull
    private BotAnswer answerAccount(long userId) {
        BotAnswer botAnswer = new BotAnswer();

        List<InlineKeyboard> inlineKeyboardList = getAccountKeyboard(userId);
        if (inlineKeyboardList != null) {
            botAnswer.setKeyboard(keyboardMessage.createinlineKeyboardMarkup(inlineKeyboardList));
            botAnswer.setMessage(Sender.TRANSACTION_ACCOUNT);
        } else {
            botAnswer.setMessage(Sender.TRANSACTION_ACCOUNT_ZERO);
        }
        return botAnswer;
    }

    @NotNull
    private BotAnswer answerConfirm(String userName, String account, BigDecimal sum) {
        BotAnswer botAnswer = new BotAnswer();
        String message = String.format(Sender.TRANSACTION_CONFIRM, userName, account, sum);

        List<InlineKeyboard> inlineKeyboardList = List.of(
                new InlineKeyboard("Отправить", "Send"),
                new InlineKeyboard("Отменить", "Cancel")
        );

        botAnswer.setMessage(message);
        botAnswer.setKeyboard(keyboardMessage.createinlineKeyboardMarkup(inlineKeyboardList));
        return botAnswer;
    }

    @NotNull
    private List<InlineKeyboard> getKeyboard(@NotNull List<UserTransactionView> userTransactionViewList) {
        List<InlineKeyboard> inlineKeyboardList = new ArrayList<>();

        for (UserTransactionView userTransactionView : userTransactionViewList) {
            inlineKeyboardList.add(new InlineKeyboard(userTransactionView.getUserName(), String.valueOf(userTransactionView.getUserDst())));
        }
        return inlineKeyboardList;
    }

    @Nullable
    private List<InlineKeyboard> getAccountKeyboard(long userId) {
        List<BalanceDto> balanceDtoList = postgresService.getBalanceList(userId);

        balanceDtoList.removeIf(balanceDto -> balanceDto.getBalance().compareTo(new BigDecimal("0")) <= 0);

        if (!balanceDtoList.isEmpty()) {
            List<InlineKeyboard> inlineKeyboardList = new ArrayList<>();
            List<AccountDto> accountDtoList = postgresService.getAccountList();
            Map<Integer, AccountDto> accountDtoMap = new HashMap<>();

            for (AccountDto accountDto : accountDtoList) {
                accountDtoMap.put(accountDto.getId(), accountDto);
            }

            for (BalanceDto balanceDto : balanceDtoList) {
                inlineKeyboardList.add(new InlineKeyboard(accountDtoMap.get(balanceDto.getAccount()).getTranslate(), String.valueOf(balanceDto.getAccount())));
            }

            return inlineKeyboardList;
        } else {
            return null;
        }
    }
}