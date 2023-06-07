package tgb.btc.lib.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.BotKeyboard;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.CHECKS_FOR_DATE)
public class ChecksForDate extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.nextStep(chatId, Command.SEND_CHECKS_FOR_DATE);
        responseSender.sendMessage(chatId, "Введите дату в формате 01.01.2000", BotKeyboard.REPLY_CANCEL);
    }

}
