package tgb.btc.rce.service.processors.paymenttypes;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.PAYMENT_TYPES)
public class PaymentsTypes extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId,
                                   "Меню управления типами оплаты.",
                                   MenuFactory.build(Menu.PAYMENT_TYPES, userService.isAdminByChatId(chatId)));
    }

}
