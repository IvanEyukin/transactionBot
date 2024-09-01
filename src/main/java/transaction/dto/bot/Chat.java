package transaction.dto.bot;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;

@Data
@RedisHash(value = "Chat", timeToLive = 600L)
public class Chat implements Serializable {
    @Id
    private long id;
    private Boolean hasMessage;
    private Boolean hasCallback;
    private User user;
    private Message message;
    private Callback callback;
    private Transaction transaction;
    private State state;
    private BotAnswer botAnswer;
}