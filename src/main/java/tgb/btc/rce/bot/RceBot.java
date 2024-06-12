package tgb.btc.rce.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.exception.NumberParseException;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.util.TelegramBotPropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@Service
@Slf4j
public class RceBot extends TelegramLongPollingBot {

    private final IUpdateDispatcher updateDispatcher;

    @Autowired
    public RceBot(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public String getBotUsername() {
        return TelegramBotPropertiesUtil.getUsername();
    }

    @Override
    public String getBotToken() {
        return TelegramBotPropertiesUtil.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            try {
                updateDispatcher.dispatch(update);
            } catch (NumberParseException e) {
                execute(SendMessage.builder()
                        .chatId(UpdateUtil.getChatId(update).toString())
                        .text("Неверный формат.")
                        .build());
            } catch (Exception e) {
                Long time = System.currentTimeMillis();
                String message = "Произошла ошибка." + System.lineSeparator() +
                        time + System.lineSeparator() +
                        "Введите /start для выхода в главное меню.";

                execute(SendMessage.builder().chatId(UpdateUtil.getChatId(update).toString()).text(message).build());
                log.debug(String.format("Произошла ошибка. %s. Описание ошибки: %s", time, e.getMessage()
                        + System.lineSeparator() + ExceptionUtils.getFullStackTrace(e)));
            }
        } catch (TelegramApiException ignored) {
        }
    }

}
