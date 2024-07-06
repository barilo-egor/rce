package tgb.btc.rce.service.processors.personaldiscounts.buy;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.MessageTemplate;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.PERSONAL_BUY_DISCOUNT)
public class ChangePersonalBuyDiscountProcessor extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, MessageTemplate.ASK_CHAT_ID);
        modifyUserService.nextStep(chatId, Command.PERSONAL_BUY_DISCOUNT.name());
    }
}
