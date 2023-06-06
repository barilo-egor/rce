package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.DealService;
import tgb.btc.lib.service.processors.support.ExchangeService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.BUY_BITCOIN)
public class BuyBitcoin extends Processor {

    private DealService dealService;

    private DealProcessor dealProcessor;

    private ExchangeService exchangeService;

    @Autowired
    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Autowired
    public void setDealProcessor(DealProcessor dealProcessor) {
        this.dealProcessor = dealProcessor;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (exchangeService.alreadyHasDeal(chatId)) return;
        dealService.createNewDeal(DealType.BUY, chatId);
        dealProcessor.run(update);
    }

}
