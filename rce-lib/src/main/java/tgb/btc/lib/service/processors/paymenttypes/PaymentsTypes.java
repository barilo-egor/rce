package tgb.btc.lib.service.processors.paymenttypes;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.Menu;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.MenuFactory;
import tgb.btc.lib.util.UpdateUtil;

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
