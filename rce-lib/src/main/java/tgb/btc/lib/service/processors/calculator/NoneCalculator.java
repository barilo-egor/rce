package tgb.btc.lib.service.processors.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.IUpdateDispatcher;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.processors.DealProcessor;
import tgb.btc.lib.service.processors.support.ExchangeService;
import tgb.btc.lib.util.CallbackQueryUtil;
import tgb.btc.lib.util.UpdateUtil;

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
            userRepository.updateStepAndCommandByChatId(chatId, Command.DEAL, DealProcessor.AFTER_CALCULATOR_STEP);
            dealProcessor.run(update);
            return;
        }
        if (!exchangeService.calculateDealAmount(chatId, UpdateUtil.getBigDecimalFromText(update))) return;
        userRepository.updateStepAndCommandByChatId(chatId, Command.DEAL, DealProcessor.AFTER_CALCULATOR_STEP);
        updateDispatcher.runProcessor(Command.DEAL, chatId, update);
    }
}
