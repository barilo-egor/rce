package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.process.IDealPoolService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.ADD_TO_POOL)
@Slf4j
public class AddToPool extends Processor {

    private final IDealPoolService dealPoolService;

    @Autowired
    public AddToPool(IDealPoolService dealPoolService) {
        this.dealPoolService = dealPoolService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long dealPid = callbackQueryService.getSplitLongData(update, 1);
        dealPoolService.addToPool(dealPid, chatId);
        log.debug("Пользователь chatId={} добавил сделку {} в пул.", chatId, dealPid);
        responseSender.deleteCallbackMessageIfExists(update);
        responseSender.sendMessage(chatId, "Сделка №" + dealPid + " добавлена в пул.");
    }
}
