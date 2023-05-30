package tgb.btc.rce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.processors.DealProcessor;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SELL_BITCOIN)
public class SellBitcoinNew extends Processor {

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
