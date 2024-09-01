package transaction.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.yml")
public class BotConfig {
    @Value("${spring.bot.name}")
    String botName;
    @Value("${spring.bot.token}")
    String token;
    @Value("${spring.bot.owner}")
    Long ownerId;
}