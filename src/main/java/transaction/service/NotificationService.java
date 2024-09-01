package transaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import transaction.dto.bot.BotAnswer;
import transaction.dto.bot.Chat;
import transaction.dto.database.AccountDto;
import transaction.dto.database.AdminNotification;
import transaction.dto.database.UserDto;
import transaction.entitie.InlineKeyboard;
import transaction.handler.message.HandlerBalance;
import transaction.sender.Sender;
import transaction.service.database.PostgresService;
import transaction.service.database.RedisService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final MessageService messageService;
    private final KeyboardMessage keyboardMessage;
    private final PostgresService postgresService;
    private final RedisService redisService;
    private final HandlerBalance handlerBalance;

    public void notification(Chat chat) {
        notificationUserSrc(chat, Sender.NOTIFICATION_USER_SRC);
        notificationUserDst(chat, Sender.NOTIFICATION_USER_DST);
        notificationUserAdmin(chat, "Create");
    }

    public void notificationDelete(Chat chat) {
        notificationUserSrc(chat, Sender.NOTIFICATION_USER_SRC_CANCELLATION);
        notificationUserDst(chat, Sender.NOTIFICATION_USER_DST_CANCELLATION);
        notificationUserAdmin(chat, "Delete");
    }

    public void adminNotification(@NotNull Chat chat) {
        if (chat.getTransaction() != null) {
            messageService.sendMessage(chat.getTransaction().getUserDst(), Sender.ADMIN_NOTIFICATION_UPDATE_BALANCE);
        } else {
            List<UserDto> userDtoList = postgresService.getUserList();
            for (UserDto userDto : userDtoList) {
                messageService.sendMessage(userDto.getId(), Sender.ADMIN_NOTIFICATION_UPDATE_BALANCE);
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private void notificationUserSrc(@NotNull Chat chat, String message) {
        UserDto userDto = postgresService.getUser(chat.getTransaction().getUserDst());
        AccountDto accountDto = postgresService.getAccount(chat.getTransaction().getAccount());
        String text = String.format(message,
                userDto.getUserName(),
                accountDto.getTranslate(),
                chat.getTransaction().getSum(),
                handlerBalance.getBalance(chat.getTransaction().getUserSrc(), chat.getTransaction().getAccount())
        );
        messageService.sendMessage(chat.getTransaction().getUserSrc(), text);
    }

    private void notificationUserDst(@NotNull Chat chat, String message) {
        UserDto userDto = postgresService.getUser(chat.getTransaction().getUserSrc());
        AccountDto accountDto = postgresService.getAccount(chat.getTransaction().getAccount());
        String text = String.format(message,
                chat.getTransaction().getSum(),
                accountDto.getTranslate(),
                userDto.getUserName(),
                handlerBalance.getBalance(chat.getTransaction().getUserDst(), chat.getTransaction().getAccount())
        );
        messageService.sendMessage(chat.getTransaction().getUserDst(), text);
    }

    private void notificationUserAdmin(@NotNull Chat chat, String type) {
        List<UserDto> userDtoList = postgresService.getAdminList();
        UserDto userDstDto = postgresService.getUser(chat.getTransaction().getUserDst());
        AccountDto accountDto = postgresService.getAccount(chat.getTransaction().getAccount());

        if (userDtoList != null) {
            if (type.equals("Create")) {
                messageAdminCreate(chat, userDtoList, userDstDto, accountDto);
            } else {
                messageAdminDelete(chat, userDtoList, userDstDto, accountDto);
            }
        }
    }

    private void messageAdminCreate(@NotNull Chat chat, @NotNull List<UserDto> userDtoList, @NotNull UserDto userDstDto, @NotNull AccountDto accountDto) {
        String text = String.format(Sender.NOTIFICATION_USER_ADMIN,
                chat.getUser().getUserName(),
                userDstDto.getUserName(),
                accountDto.getTranslate(),
                chat.getTransaction().getSum()
        );

        List<AdminNotification> adminNotificationList = new ArrayList<>();

        for (UserDto userDto : userDtoList) {
            InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMessage.createinlineKeyboardMarkup(new InlineKeyboard("Отменить", String.format("CancelAdminTransaction.%s", chat.getTransaction().getGuid())));
            int messageId = messageService.sendMessage(userDto.getId(), text, inlineKeyboardMarkup);
            adminNotificationList.add(new AdminNotification(userDto.getId(), messageId));
        }
        redisService.setAdminNotification(adminNotificationList);
    }

    private void messageAdminDelete(@NotNull Chat chat, List<UserDto> userDtoList, @NotNull UserDto userDstDto, @NotNull AccountDto accountDto) {
        String text = String.format(Sender.NOTIFICATION_USER_ADMIN_CANCELLATION,
                chat.getUser().getUserName(),
                userDstDto.getUserName(),
                accountDto.getTranslate(),
                chat.getTransaction().getSum()
        );

        for (AdminNotification adminNotification : redisService.getAdminNotification()) {
            messageService.deleteMessage(adminNotification.getUserId(), adminNotification.getMessageId());
        }

        for (UserDto userDto : userDtoList) {
            messageService.sendMessage(userDto.getId(), text);
        }
    }
}