package tgb.btc.rce.service.handler.impl.state.deal;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class InlineQueryCalculatorHandler implements IStateHandler {


    private final IUpdateService updateService;

    private final ExchangeService exchangeService;

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    private final DealHandler dealHandler;

    private final IRedisStringService redisStringService;

    public InlineQueryCalculatorHandler(IUpdateService updateService, ExchangeService exchangeService,
                                        ICallbackDataService callbackDataService,
                                        IRedisUserStateService redisUserStateService, DealHandler dealHandler,
                                        IRedisStringService redisStringService) {
        this.updateService = updateService;
        this.exchangeService = exchangeService;
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
        this.dealHandler = dealHandler;
        this.redisStringService = redisStringService;
    }

    @Override
    public void handle(Update update) {
        Long chatId = UpdateType.getChatId(update);
        if (update.hasCallbackQuery() && callbackDataService.isCallbackQueryData(CallbackQueryData.BACK, update.getCallbackQuery().getData())) {
            redisUserStateService.save(chatId, UserState.DEAL);
            redisStringService.saveStep(chatId, DealHandler.AFTER_CALCULATOR_STEP);
            dealHandler.handle(update);
            return;
        }
        if (update.hasMessage()) {
            if (!exchangeService.calculateDealAmount(chatId, updateService.getBigDecimalFromText(update))) return;
            redisUserStateService.save(chatId, UserState.DEAL);
            redisStringService.saveStep(chatId, DealHandler.AFTER_CALCULATOR_STEP);
            dealHandler.handle(update);
        } else if (update.hasInlineQuery()) exchangeService.calculateForInlineQuery(update);
    }

    @Override
    public UserState getUserState() {
        return UserState.INLINE_QUERY_CALCULATOR;
    }
}
