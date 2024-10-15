package tgb.btc.rce.service.processors.admin.requests.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.service.process.IDealPoolService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.CONFIRM_CLEAR_POOL)
public class ConfirmClearPool extends Processor {

    private final IDealPoolService dealPoolService;

    @Autowired
    public ConfirmClearPool(IDealPoolService dealPoolService) {
        this.dealPoolService = dealPoolService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        dealPoolService.clearPool(CryptoCurrency.BITCOIN);
        Integer poolMessageId = callbackQueryService.getSplitIntData(update, 1);
        responseSender.deleteMessage(chatId, poolMessageId);
        responseSender.deleteCallbackMessageIfExists(update);
        responseSender.sendMessage(chatId, "Пул BTC очищен.");
    }
}
