package tgb.btc.rce.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.Objects;

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
        try {
            eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
        } catch (NumberFormatException e) {
            execute(SendMessage.builder()
                    .chatId(Objects.requireNonNull(UpdateType.getChatId(update)).toString())
                    .text("Неверный формат.")
                    .build());
        } catch (Exception e) {
            Long time = System.currentTimeMillis();
            log.debug("{} Необработанная ошибка.", time, e);
            String message = "Произошла ошибка." + System.lineSeparator() +
                    time + System.lineSeparator() +
                    "Введите /start для выхода в главное меню.";

            try {
                execute(SendMessage.builder()
                        .chatId(Objects.requireNonNull(UpdateType.getChatId(update)).toString())
                        .text(message)
                        .build());
            } catch (Exception ignored) {
            }
        }
    }

}
