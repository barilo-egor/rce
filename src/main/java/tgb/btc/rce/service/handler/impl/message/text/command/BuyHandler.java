package tgb.btc.rce.service.handler.impl.message.text.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.bean.bot.deal.ModifyDealService;
import tgb.btc.library.service.properties.FunctionsPropertiesReader;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.impl.state.DealHandler;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.processors.support.ExchangeService;

import java.util.List;

@Service
@Slf4j
public class BuyHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final DealHandler dealHandler;

    private final ExchangeService exchangeService;

    private final ModifyDealService modifyDealService;

    private final FunctionsPropertiesReader functionsPropertiesReader;

    public BuyHandler(IResponseSender responseSender, DealHandler dealHandler, ExchangeService exchangeService,
                      ModifyDealService modifyDealService, FunctionsPropertiesReader functionsPropertiesReader) {
        this.responseSender = responseSender;
        this.dealHandler = dealHandler;
        this.exchangeService = exchangeService;
        this.modifyDealService = modifyDealService;
        this.functionsPropertiesReader = functionsPropertiesReader;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (!checkAllowedDealsCount(chatId)) {
            responseSender.sendMessage(chatId, "Достигнут лимит количества сделок!");
            return;
        }
        deleteUnfinishedDeal(chatId);

        modifyDealService.createNewDeal(DealType.BUY, chatId);
        Update update = new Update();
        update.setMessage(message);
        dealHandler.handle(update);
    }

    private boolean checkAllowedDealsCount(Long chatId) {
        String allowedDealsCountStr = functionsPropertiesReader.getString("allowed.deals.count");
        Integer allowedDealsCount;
        try {
            allowedDealsCount = Integer.parseInt(allowedDealsCountStr);
        } catch (NumberFormatException e) {
            throw new BaseException("Допустимое кол-во сделок должно иметь целочисленное значение.");
        }
        if (allowedDealsCount < 0) throw new BaseException("Допустимое кол-во сделок не может быть < 0.");
        if (allowedDealsCount == 0) return Boolean.TRUE;
        if (exchangeService.getCountNotFinishedDeals(chatId) < allowedDealsCount) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private void deleteUnfinishedDeal(Long chatId) {
        List<Long> pids = exchangeService.getListNewDeal(chatId);
        if (CollectionUtils.isNotEmpty(pids)) {
            log.info("Найдено {} незавершенных сделок", pids.size());
            exchangeService.deleteByPidIn(pids);
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BUY_BITCOIN;
    }

}
