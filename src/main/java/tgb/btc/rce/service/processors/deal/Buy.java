package tgb.btc.rce.service.processors.deal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.ExchangeService;

import java.util.List;

@CommandProcessor(command = Command.BUY_BITCOIN)
@Slf4j
public class Buy extends Processor {

    private IModifyDealService modifyDealService;

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
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (!checkAllowedDealsCount(chatId)) {
            responseSender.sendMessage(chatId, "Достигнут лимит количества сделок!");
            return;
        }
        deleteUnfinishedDeal(chatId);

        modifyDealService.createNewDeal(DealType.BUY, chatId);
        dealProcessor.run(update);
    }

    private boolean checkAllowedDealsCount(Long chatId) {
        String allowedDealsCountStr = PropertiesPath.FUNCTIONS_PROPERTIES.getString("allowed.deals.count");
        Integer allowedDealsCount;
        try {
            allowedDealsCount = Integer.parseInt(allowedDealsCountStr);
        } catch (NumberFormatException e) {
            throw new BaseException("Допустимое кол-во сделок должно иметь целочисленное значение.");
        }
        if (allowedDealsCount < 0) throw new BaseException("Допустимое кол-во сделок не может быть < 0.");
        if (allowedDealsCount == 0) return Boolean.TRUE;
        if (exchangeService.getCountFinishedDeal(chatId) < allowedDealsCount) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private void deleteUnfinishedDeal(Long chatId) {
        List<Long> pids = exchangeService.getListNewDeal(chatId);
        if (CollectionUtils.isNotEmpty(pids)) {
            log.info(String.format("Найдено %s незавершенных сделок", pids.size()));
            exchangeService.deleteByPidIn(pids);
        }
    }

}
