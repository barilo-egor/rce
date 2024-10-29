package tgb.btc.rce.service.processors.admin.hidden;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.process.IDealPoolService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.admin.requests.deal.BitcoinPool;

@CommandProcessor(command = Command.DELETE_FROM_POOL)
public class DeleteFromPool extends Processor {

    private IDealPoolService dealPoolService;

    private BitcoinPool bitcoinPool;

    @Autowired
    public DeleteFromPool(IDealPoolService dealPoolService, BitcoinPool bitcoinPool) {
        this.dealPoolService = dealPoolService;
        this.bitcoinPool = bitcoinPool;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long dealPid = Long.parseLong(updateService.getMessageText(update).split(" ")[1]);
        responseSender.deleteCallbackMessageIfExists(update);
        dealPoolService.deleteFromPool(dealPid, chatId);
        responseSender.sendMessage(chatId, "Сделка №" + dealPid + " удалена из пула.");
        bitcoinPool.run(update);
    }
}
