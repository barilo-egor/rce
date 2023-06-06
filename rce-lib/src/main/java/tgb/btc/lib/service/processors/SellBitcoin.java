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

@CommandProcessor(command = Command.SELL_BITCOIN)
public class SellBitcoin extends Processor {

    private ExchangeService exchangeService;

    private DealProcessor dealProcessor;

    private DealService dealService;

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
        dealService.createNewDeal(DealType.SELL, chatId);
        dealProcessor.run(update);
    }

}
