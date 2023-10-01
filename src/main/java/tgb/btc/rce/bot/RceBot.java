package tgb.btc.rce.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.exception.NumberParseException;
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
                execute(SendMessage.builder()
                                .chatId(UpdateUtil.getChatId(update).toString())
                                .text("Что-то пошло не так: " + e.getMessage() + "\n" + ExceptionUtils.getFullStackTrace(
                                        e))
                                .build());
                log.error("Ошибка", e);

            }
        } catch (TelegramApiException ignored) {
        }
    }

}
