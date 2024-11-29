package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.processors.deal.DealProcessor;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IUpdateDispatcher;

@Service
public class NoneCalculatorHandler implements IStateHandler {

    private final ExchangeService exchangeService;

    private final DealProcessor dealProcessor;

    private final IUpdateDispatcher updateDispatcher;

    private final IModifyUserService modifyUserService;

    private final ICallbackDataService callbackDataService;

    private final IUpdateService updateService;

    private final IRedisUserStateService redisUserStateService;

    public NoneCalculatorHandler(ExchangeService exchangeService, DealProcessor dealProcessor,
                                 IUpdateDispatcher updateDispatcher, IModifyUserService modifyUserService,
                                 ICallbackDataService callbackDataService, IUpdateService updateService,
                                 IRedisUserStateService redisUserStateService) {
        this.exchangeService = exchangeService;
        this.dealProcessor = dealProcessor;
        this.updateDispatcher = updateDispatcher;
        this.modifyUserService = modifyUserService;
        this.callbackDataService = callbackDataService;
        this.updateService = updateService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Update update) {
        if (dealProcessor.isMainMenuCommand(update)) return;
        Long chatId = UpdateType.getChatId(update);
        if (update.hasCallbackQuery() && callbackDataService.isCallbackQueryData(CallbackQueryData.BACK, update.getCallbackQuery().getData())) {
            modifyUserService.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
            dealProcessor.run(update);
            return;
        }
        if (!exchangeService.calculateDealAmount(chatId, updateService.getBigDecimalFromText(update))) return;
        modifyUserService.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
        redisUserStateService.delete(chatId);
        dealProcessor.process(update);
    }

    @Override
    public UserState getUserState() {
        return UserState.NONE_CALCULATOR;
    }
}
