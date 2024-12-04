package tgb.btc.rce.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.vo.TelegramUpdateEvent;

@Service
@Slf4j
public class RceBot extends TelegramLongPollingBot {

    private final ApplicationEventPublisher eventPublisher;

    private final String username;

    public RceBot(ApplicationEventPublisher eventPublisher,
                  @Value("${bot.username}") String username,
                  @Value("${bot.token}") String token) {
        super(token);
        this.username = username;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
    }

}
