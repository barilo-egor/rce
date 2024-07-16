package tgb.btc.rce.service.processors.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.bot.WebAPI;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.LOGOUT)
public class WebLogoutProcessor extends Processor {

    private WebAPI webAPI;

    @Autowired
    public void setWebAPI(WebAPI webAPI) {
        this.webAPI = webAPI;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        webAPI.logout(chatId);
        responseSender.deleteCallbackMessageIfExists(update);
        responseSender.sendMessage(chatId, "Сессия закрыта.");
    }
}
