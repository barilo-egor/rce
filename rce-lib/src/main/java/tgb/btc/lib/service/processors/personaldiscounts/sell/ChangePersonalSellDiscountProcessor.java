package tgb.btc.lib.service.processors.personaldiscounts.sell;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.MessageTemplate;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.PERSONAL_SELL_DISCOUNT)
public class ChangePersonalSellDiscountProcessor extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, MessageTemplate.ASK_CHAT_ID);
        userRepository.nextStep(chatId, Command.PERSONAL_SELL_DISCOUNT);
    }
}
