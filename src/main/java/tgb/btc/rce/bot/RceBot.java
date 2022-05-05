package tgb.btc.rce.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.util.BotPropertiesUtil;

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
        updateDispatcher.dispatch(update);
    }
}
