package tgb.btc.rce.service.processors.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.bot.WebAPI;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;

@CommandProcessor(command = Command.SUBMIT_REGISTER)
public class SubmitRegisterProcessor extends Processor {

    private WebAPI webAPI;

    @Autowired
    public SubmitRegisterProcessor(WebAPI webAPI) {
        this.webAPI = webAPI;
    }

    @Override
    public void run(Update update) {
        Long chatId = CallbackQueryUtil.getSplitLongData(update, 1);
        webAPI.submitChatId(chatId);
        responseSender.deleteCallbackMessageIfExists(update);
    }
}
