package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHECKS_FOR_DATE)
public class ChecksForDate extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userRepository.nextStep(chatId, Command.SEND_CHECKS_FOR_DATE.name());
        responseSender.sendMessage(chatId, "Введите дату в формате 01.01.2000", BotKeyboard.REPLY_CANCEL);
    }

}
