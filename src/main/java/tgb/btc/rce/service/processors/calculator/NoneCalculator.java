package tgb.btc.rce.service.processors.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.DealProcessor;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NONE_CALCULATOR, step = 1)
public class NoneCalculator extends Processor {

    private ExchangeService exchangeService;

    private DealProcessor dealProcessor;

    private IUpdateDispatcher updateDispatcher;


    @Autowired
    public void setUpdateDispatcher(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Autowired
    public void setDealProcessor(DealProcessor dealProcessor) {
        this.dealProcessor = dealProcessor;
    }

    @Autowired
    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    public void run(Update update) {
        if (dealProcessor.isMainMenuCommand(update)) return;
        Long chatId = UpdateUtil.getChatId(update);
        if (CallbackQueryUtil.isBack(update)) {
            userRepository.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
            dealProcessor.run(update);
            return;
        }
        if (!exchangeService.calculateDealAmount(chatId, UpdateUtil.getBigDecimalFromText(update))) return;
        userRepository.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
        updateDispatcher.runProcessor(Command.DEAL, chatId, update);
    }
}
