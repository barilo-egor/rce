package tgb.btc.rce.service.processors.paymenttypes.create;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE)
public class NewPaymentType extends Processor {

    public static final String ENTER_NAME = "Введите название нового типа оплаты.";

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, ENTER_NAME, BotKeyboard.REPLY_CANCEL);
        modifyUserService.nextStep(chatId, Command.NEW_PAYMENT_TYPE.name());
    }

}
