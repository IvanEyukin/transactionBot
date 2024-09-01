package transaction.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import transaction.config.BotConfig;

@Component
@Slf4j
public class MessageService {
    private final TelegramClient client;

    public MessageService(@NotNull BotConfig config) {
        this.client = new OkHttpTelegramClient(config.getToken());
    }

    public Integer sendMessage(long chantId, String text) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chantId)
                .text(text)
                .build();
        try {
            log.debug("Send message chat: {}, text: {}", message.getChatId(), message.getText());
            return client.execute(message).getMessageId();
        } catch (TelegramApiException e) {
            log.error("Error send message: ", e);
            return null;
        }
    }

    public Integer sendMessage(long chantId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chantId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        try {
            log.debug("Send message chat: {}, text: {}, keyboard: {}", message.getChatId(), message.getText(), message.getReplyMarkup());
            return client.execute(message).getMessageId();
        } catch (TelegramApiException e) {
            log.error("Error send message: ", e);
            return null;
        }
    }

    public void updateMessage(long chatId, Integer messageId) {
        EditMessageReplyMarkup message = EditMessageReplyMarkup
                .builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(null)
                .build();
        try {
            client.execute(message);
            log.debug("Update message chat: {}, message: {}", message.getChatId(), message.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Error update message: ", e);
        }
    }

    public void deleteMessage(long chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage
                .builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
        try {
            client.execute(deleteMessage);
            log.debug("Delete message: {}, {}", deleteMessage.getChatId(), deleteMessage.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Error delete message: ", e);
        }
    }

    public void answerCallback(String callbackId) {
        AnswerCallbackQuery answerCallback = AnswerCallbackQuery
                .builder()
                .callbackQueryId(callbackId)
                .build();
        try {
            client.execute(answerCallback);
            log.debug("Answer callback: {}", callbackId);
        } catch (TelegramApiException e) {
            log.error("Error answer callback: ", e);
        }
    }
}