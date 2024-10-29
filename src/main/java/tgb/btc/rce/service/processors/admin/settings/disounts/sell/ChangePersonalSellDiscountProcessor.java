package tgb.btc.rce.service.processors.admin.settings.disounts.sell;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.PERSONAL_SELL_DISCOUNT)
public class ChangePersonalSellDiscountProcessor extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        responseSender.sendMessage(chatId, "Введите чат айди пользователя.", keyboardService.getReplyCancel());
        modifyUserService.nextStep(chatId, Command.PERSONAL_SELL_DISCOUNT.name());
    }
}
