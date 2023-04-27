package tgb.btc.rce.service.processors.paymenttypes.requisite.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE)
public class NewPaymentTypeRequisite extends Processor {

    @Autowired
    public NewPaymentTypeRequisite(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, KeyboardUtil.getBuyOrSell());
        userService.nextStep(chatId, Command.NEW_PAYMENT_TYPE_REQUISITE);
    }

}
