package transaction.service.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import transaction.dto.bot.Chat;
import transaction.dto.database.AdminNotification;
import transaction.mapper.ChatMapper;
import transaction.repository.AdminNotificationRepository;
import transaction.repository.ChatRepository;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final ChatRepository chatRepository;
    private final AdminNotificationRepository adminNotificationRepository;
    private final ChatMapper chatMapper;

    public void setChatRepository(Chat chat) {
        chatRepository.save(chatMapper.chatRepositoryMapper(chat));
        log.debug("Save redis request: {}", chat);
    }

    public Chat getChatRepository(@NotNull Chat chat) {
        Boolean repositoryIsPresent = chatRepository.findById(chat.getId()).isPresent();
        log.debug("Redis request: {}", chat.getId());
        log.debug("Redis response: {}", repositoryIsPresent);

        if (repositoryIsPresent) {
            Chat repositoryChat = chatRepository.findById(chat.getId()).get();
            chat = chatMapper.chatRepositoryMapper(chat, repositoryChat);
            log.debug("Redis request: {}", chat.getId());
            log.debug("Redis response: {}", repositoryChat);
        }
        return chat;
    }

    public void setAdminNotification(List<AdminNotification> adminNotificationList) {
        adminNotificationRepository.saveAll(adminNotificationList);
        log.debug("Save redis request: {}", adminNotificationList);
    }

    public List<AdminNotification> getAdminNotification() {
        List<AdminNotification> adminNotificationList = adminNotificationRepository.findAll();
        log.debug("Redis response: {}", adminNotificationList);
        return adminNotificationList;
    }
}