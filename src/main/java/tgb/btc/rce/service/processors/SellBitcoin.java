package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SELL_BITCOIN)
public class SellBitcoin extends Processor {

    private DealProcessor dealProcessor;


    private IModifyDealService modifyDealService;

    @Autowired
    public void setDealProcessor(DealProcessor dealProcessor) {
        this.dealProcessor = dealProcessor;
    }

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);

        modifyDealService.createNewDeal(DealType.SELL, chatId);
        dealProcessor.run(update);
    }

}
