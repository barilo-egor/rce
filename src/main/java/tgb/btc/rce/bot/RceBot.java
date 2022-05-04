package tgb.btc.rce.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.util.BotPropertiesUtil;

@Service
public class RceBot extends TelegramLongPollingBot {
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

    }
}
