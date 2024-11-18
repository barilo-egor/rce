package tgb.btc.rce.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.service.IGroupUpdateDispatcher;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.util.ITelegramPropertiesService;
import tgb.btc.rce.service.util.IUpdateDispatcher;

@Service
@Slf4j
public class RceBot extends TelegramLongPollingBot {

    private final IUpdateDispatcher updateDispatcher;

    private IGroupUpdateDispatcher groupUpdateDispatcher;

    private ITelegramPropertiesService telegramPropertiesService;

    private IUpdateService updateService;

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setTelegramPropertiesService(ITelegramPropertiesService telegramPropertiesService) {
        this.telegramPropertiesService = telegramPropertiesService;
    }

    @Autowired
    public void setGroupUpdateDispatcher(IGroupUpdateDispatcher groupUpdateDispatcher) {
        this.groupUpdateDispatcher = groupUpdateDispatcher;
    }

    @Autowired
    public RceBot(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public String getBotUsername() {
        return telegramPropertiesService.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramPropertiesService.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (updateService.isGroupMessage(update))
                groupUpdateDispatcher.dispatch(update);
            else {
                updateDispatcher.dispatch(update);
            }
        } catch (NumberFormatException e) {
            execute(SendMessage.builder()
                    .chatId(updateService.getChatId(update).toString())
                    .text("Неверный формат.")
                    .build());
        } catch (Exception e) {
            Long time = System.currentTimeMillis();
            log.debug("{} Необработанная ошибка.", time, e);
            String message = "Произошла ошибка." + System.lineSeparator() +
                    time + System.lineSeparator() +
                    "Введите /start для выхода в главное меню.";

            try {
                execute(SendMessage.builder().chatId(updateService.getChatId(update).toString()).text(message).build());
            } catch (Exception ignored) {
            }
        }
    }
}
