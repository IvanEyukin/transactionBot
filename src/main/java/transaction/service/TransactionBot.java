package transaction.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import transaction.config.BotConfig;
import transaction.route.Route;

@Component
public class TransactionBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotConfig config;
    private final Route route;

    private TransactionBot(BotConfig config, Route route) {
        this.config = config;
        this.route = route;
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        route.route(update);
    }
}