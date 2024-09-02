package transaction.handler;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import transaction.handler.message.HandlerAdmin;
import transaction.sender.UserCommand;
import transaction.dto.bot.BotAnswer;
import transaction.dto.bot.Chat;
import transaction.dto.bot.State;
import transaction.entitie.Status;
import transaction.handler.message.HandlerBalance;
import transaction.handler.message.HandlerName;
import transaction.handler.message.HandlerStart;
import transaction.sender.Sender;
import transaction.service.MessageService;
import transaction.service.NotificationService;
import transaction.service.database.RedisService;

@Service
@RequiredArgsConstructor
public class MessageHandler {

    private final MessageService messageService;
    private final RedisService redisService;
    private final HandlerStart handlerStart;
    private final HandlerName handlerName;
    private final HandlerBalance handlerBalance;
    private final HandlerTransaction handlerTransaction;
    private final HandlerAdmin handlerAdmin;
    private final NotificationService notificationService;

    public void handler(Chat chat) {
        chat = redisService.getChatRepository(chat);

        if (chat.getBotAnswer() != null && chat.getBotAnswer().getMessageHasKeyboard()) {
            messageService.updateMessage(chat.getUser().getId(), chat.getBotAnswer().getId());
        }

        if ((chat.getState() == null || chat.getState().getState() == null) ||
                chat.getState().getState().equals(Status.WaitingUserCommand) ||
                chat.getMessage().getMessage().equals(UserCommand.start) ||
                chat.getMessage().getMessage().equals(UserCommand.balance) ||
                chat.getMessage().getMessage().equals(UserCommand.send) ||
                chat.getMessage().getMessage().equals(UserCommand.rename)) {
            chat.setState(setStateInMessage(chat));
        }

        State state = new State();
        switch (chat.getState().getState()) {
            case Start -> {
                chat.setBotAnswer(handlerStart.execute(chat));
                if (chat.getUser().getUserName() == null) {
                    state.updateState(Status.WaitingName);
                } else {
                    state.updateState(Status.WaitingUserCommand);
                }
            }
            case Rename -> {
                chat.setBotAnswer(handlerName.answerName());
                state.updateState(Status.WaitingName);
            }
            case WaitingName -> {
                chat = handlerName.saveUserName(chat);
                if (!chat.getBotAnswer().getMessage().equals(Sender.USER_NAME_ERROR) && !chat.getBotAnswer().getMessage().equals(Sender.USER_NAME_INVALID)) {
                    state.updateState(Status.WaitingUserCommand);
                }
            }
            case Balance -> {
                chat.setBotAnswer(handlerBalance.getBalanceList(chat));
                state.updateState(Status.WaitingUserCommand);
            }
            case Transaction -> {
                chat.setBotAnswer(handlerTransaction.startTransaction(chat));
                state.updateState(Status.WaitingTransactionUser);
            }
            case WaitingTransactionUser -> {
                chat = handlerTransaction.setUserDst(chat);
                if (chat.getBotAnswer().getMessage().equals(Sender.TRANSACTION_ACCOUNT_ZERO)) {
                    state.updateState(Status.WaitingUserCommand);
                } else if (!chat.getBotAnswer().getMessage().equals(Sender.TRANSACTION_USER_ERROR)) {
                    state.updateState(Status.WaitingTransactionAccount);
                }
            }
            case WaitingTransactionBalance -> {
                chat = handlerTransaction.setBalance((chat));
                if (chat.getTransaction().getSum() != null) {
                    state.updateState(Status.WaitingTransactionConfirm);
                }
            }
            case Error -> {
                BotAnswer botAnswer = new BotAnswer();
                botAnswer.setMessage(Sender.ERROR);
                chat.setBotAnswer(botAnswer);
                state.updateState(Status.WaitingUserCommand);
            }
            case AdminMenu -> {
                if (handlerAdmin.checkUserIsAdmin(chat)) {
                    chat.setBotAnswer(handlerAdmin.menu());
                    state.updateState(Status.WaitingAdminCommand);
                }
            }
            case WaitingAdminBalanceUser -> {
                chat = handlerTransaction.setUserDst(chat);
                if (!chat.getBotAnswer().getMessage().equals(Sender.TRANSACTION_USER_ERROR)) {
                    chat.setBotAnswer(handlerAdmin.answerUserAccount());
                    state.updateState(Status.WaitingAdminBalanceAccount);
                }
            }
            case WaitingAdminBalance -> {
                handlerAdmin.updateBalance(chat);
                notificationService.adminNotification(chat);
                chat.setBotAnswer(null);
                state.updateState(Status.WaitingUserCommand);
            }
        }
        if (state.getState() != null) {
            chat.setState(state);
        }
        if (chat.getBotAnswer() != null) {
            chat.getBotAnswer().setId(sendMessage(chat));
        }
        redisService.setChatRepository(chat);
    }

    @NotNull
    private State setStateInMessage(@NotNull Chat chat) {
        State state = new State();

        switch (chat.getMessage().getMessage()) {
            case(UserCommand.start) -> state.updateState(Status.Start);
            case(UserCommand.balance) -> state.updateState(Status.Balance);
            case(UserCommand.send) -> state.updateState(Status.Transaction);
            case(UserCommand.rename) -> state.updateState(Status.Rename);
            case(UserCommand.admin) -> state.updateState(Status.AdminMenu);
            default -> state.updateState(Status.Error);
        }

        return state;
    }

    private Integer sendMessage(@NotNull Chat chat) {
        if (chat.getBotAnswer().getKeyboard() == null) {
            return messageService.sendMessage(chat.getUser().getId(), chat.getBotAnswer().getMessage());
        } else {
            return messageService.sendMessage(chat.getUser().getId(), chat.getBotAnswer().getMessage(), chat.getBotAnswer().getKeyboard());
        }
    }
}