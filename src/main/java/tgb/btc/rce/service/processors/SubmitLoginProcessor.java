package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.bot.WebAPI;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.vo.InlineButton;

@CommandProcessor(command = Command.SUBMIT_LOGIN)
public class SubmitLoginProcessor extends Processor {

    private WebAPI webAPI;

    @Autowired
    public SubmitLoginProcessor(WebAPI webAPI) {
        this.webAPI = webAPI;
    }

    @Override
    public void run(Update update) {
        Long chatId = CallbackQueryUtil.getSplitLongData(update, 1);
        webAPI.submitLogin(chatId);
        responseSender.deleteCallbackMessageIfExists(update);
        responseSender.sendMessage(chatId, "Вы можете, если потребуется, закрыть сессию по кнопке ниже.",
                InlineButton.builder()
                        .text("Закрыть сессию")
                        .data(Command.LOGOUT.name())
                        .build());
    }
}
