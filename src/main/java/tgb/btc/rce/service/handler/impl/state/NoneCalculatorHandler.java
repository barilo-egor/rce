package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.bean.bot.user.ModifyUserService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class NoneCalculatorHandler implements IStateHandler {

    private final ExchangeService exchangeService;

    private final DealHandler dealHandler;

    private final ICallbackDataService callbackDataService;

    private final IUpdateService updateService;

    private final IRedisUserStateService redisUserStateService;

    private final ModifyUserService modifyUserService;

    public NoneCalculatorHandler(ExchangeService exchangeService, DealHandler dealHandler,
                                 ICallbackDataService callbackDataService, IUpdateService updateService,
                                 IRedisUserStateService redisUserStateService, ModifyUserService modifyUserService) {
        this.exchangeService = exchangeService;
        this.dealHandler = dealHandler;
        this.callbackDataService = callbackDataService;
        this.updateService = updateService;
        this.redisUserStateService = redisUserStateService;
        this.modifyUserService = modifyUserService;
    }

    @Override
    public void handle(Update update) {
        if (dealHandler.isMainMenuCommand(update)) return;
        Long chatId = UpdateType.getChatId(update);
        if (update.hasCallbackQuery() && callbackDataService.isCallbackQueryData(CallbackQueryData.BACK, update.getCallbackQuery().getData())) {
            redisUserStateService.save(chatId, UserState.DEAL);
            modifyUserService.updateStepByChatId(chatId, DealHandler.AFTER_CALCULATOR_STEP);
            dealHandler.handle(update);
            return;
        }
        if (!exchangeService.calculateDealAmount(chatId, updateService.getBigDecimalFromText(update))) return;
        redisUserStateService.save(chatId, UserState.DEAL);
        modifyUserService.updateStepByChatId(chatId, DealHandler.AFTER_CALCULATOR_STEP);
        dealHandler.handle(update);
    }

    @Override
    public UserState getUserState() {
        return UserState.NONE_CALCULATOR;
    }
}
