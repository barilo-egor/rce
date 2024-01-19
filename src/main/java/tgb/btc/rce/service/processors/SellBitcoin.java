package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.UpdateUtil;

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

        dealService.createNewDeal(DealType.SELL, chatId);
        dealProcessor.run(update);
    }

}
