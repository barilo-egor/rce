package tgb.btc.rce.service.processors.paymenttypes.minsum;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_MIN_SUM)
public class ChangeMinSum extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, BotKeyboard.BUY_OR_SELL);
        userService.nextStep(chatId, Command.CHANGE_MIN_SUM);
    }
}
