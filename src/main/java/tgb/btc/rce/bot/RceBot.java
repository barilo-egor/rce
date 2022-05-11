package tgb.btc.rce.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.util.BotPropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@Service
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
            updateDispatcher.dispatch(update);
        } catch (Exception e) {
            try {
                execute(SendMessage.builder()
                        .chatId(UpdateUtil.getChatId(update).toString())
                        .text("Что-то пошло не так: " + e.getMessage())
                        .build());
            } catch (TelegramApiException ignored) {
            }
        }
    }
}
