package tgb.btc.lib.service.processors.paymenttypes.create;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.BotKeyboard;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE)
public class NewPaymentType extends Processor {

    public static final String ENTER_NAME = "Введите название нового типа оплаты.";

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, ENTER_NAME, BotKeyboard.REPLY_CANCEL);
        userService.nextStep(chatId, Command.NEW_PAYMENT_TYPE);
    }

}
