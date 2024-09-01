package transaction.dto.database;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "AdminNotification", timeToLive = 600L)
public class AdminNotification {
    @Id
    private long userId;
    private int messageId;

    public AdminNotification(long userId, int messageId) {
        this.userId = userId;
        this.messageId = messageId;
    }
}