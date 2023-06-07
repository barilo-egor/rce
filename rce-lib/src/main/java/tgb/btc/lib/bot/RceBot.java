package tgb.btc.lib.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.lib.exception.NumberParseException;
import tgb.btc.lib.service.IUpdateDispatcher;
import tgb.btc.lib.util.BotPropertiesUtil;
import tgb.btc.lib.util.UpdateUtil;

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
        return BotPropertiesUtil.getUsername();
    }

    @Override
    public String getBotToken() {
        return BotPropertiesUtil.getToken();
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
