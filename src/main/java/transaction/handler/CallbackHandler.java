package transaction.handler;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import transaction.dto.bot.BotAnswer;
import transaction.dto.bot.Chat;
import transaction.dto.bot.State;
import transaction.dto.bot.Transaction;
import transaction.entitie.Status;
import transaction.handler.message.HandlerAdmin;
import transaction.sender.Sender;
import transaction.service.MessageService;
import transaction.service.NotificationService;
import transaction.service.database.RedisService;

@Service
@RequiredArgsConstructor
public class CallbackHandler {

    private final MessageService messageService;
    private final RedisService redisService;
    private final HandlerTransaction handlerTransaction;
    private final NotificationService notificationService;
    private final HandlerAdmin handlerAdmin;

    public void handler(Chat chat) {
        chat = redisService.getChatRepository(chat);

        messageService.answerCallback(chat.getCallback().getId());

        if (chat.getBotAnswer() != null) {
            messageService.updateMessage(chat.getUser().getId(), chat.getBotAnswer().getId());
        }

        State state = new State();

        if (chat.getCallback().getCallbackData().contains("CancelAdminTransaction")) {
            chat.setTransaction(handlerTransaction.adminCancellation(chat));
            notificationService.notificationDelete(chat);
        } else {
            switch (chat.getState().getState()) {
                case WaitingTransactionUser -> {
                    chat = handlerTransaction.setUserDst(chat);
                    if (chat.getBotAnswer().getMessage().equals(Sender.TRANSACTION_ACCOUNT_ZERO)) {
                        state.updateState(Status.WaitingUserCommand);
                    } else if (!chat.getBotAnswer().getMessage().equals(Sender.TRANSACTION_USER_ERROR)) {
                        state.updateState(Status.WaitingTransactionAccount);
                    }
                }
                case WaitingTransactionAccount -> {
                    chat = handlerTransaction.setAccount(chat);
                    if (chat.getTransaction().getAccount() != 0) {
                        state.updateState(Status.WaitingTransactionBalance);
                    }
                }
                case WaitingTransactionConfirm -> {
                    if (chat.getCallback().getCallbackData().equals("Send")) {
                        handlerTransaction.saveTransaction(chat);
                        notificationService.notification(chat);
                        chat.setBotAnswer(null);
                    } else {
                        chat.setTransaction(new Transaction());
                        BotAnswer botAnswer = new BotAnswer();
                        botAnswer.setMessage(Sender.TRANSACTION_CANCEL);
                        chat.setBotAnswer(botAnswer);
                    }
                    state.updateState(Status.WaitingUserCommand);
                }
                case WaitingAdminCommand -> {
                    if (chat.getCallback().getCallbackData().equals("UpdateBalanceUser")) {
                        chat.setBotAnswer(handlerAdmin.answerUser());
                        state.updateState(Status.WaitingAdminBalanceUser);
                    } else if (chat.getCallback().getCallbackData().equals("UpdateBalanceAll")) {
                        chat.setBotAnswer(handlerAdmin.answerBalance());
                        state.updateState(Status.WaitingAdminBalance);
                    } else if (chat.getCallback().getCallbackData().equals("UserList")) {
                        chat.setBotAnswer(handlerAdmin.getUserList());
                    }
                }
                case WaitingAdminBalanceAccount -> {
                    if (chat.getCallback().getCallbackData().equals("All")) {
                        chat.setBotAnswer(handlerAdmin.answerBalance());
                        state.updateState(Status.WaitingAdminBalance);
                    } else {
                        chat = handlerTransaction.setAccount(chat);
                        state.updateState(Status.WaitingAdminBalance);
                    }
                }
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

    private Integer sendMessage(@NotNull Chat chat) {
        if (chat.getBotAnswer().getKeyboard() == null) {
            return messageService.sendMessage(chat.getUser().getId(), chat.getBotAnswer().getMessage());
        } else {
            return messageService.sendMessage(chat.getUser().getId(), chat.getBotAnswer().getMessage(), chat.getBotAnswer().getKeyboard());
        }
    }
}