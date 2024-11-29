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

@Service
public class InlineQueryCalculatorHandler implements IStateHandler {

    private final DealProcessor dealProcessor;

    private final IUpdateService updateService;

    private final ExchangeService exchangeService;

    private final IModifyUserService modifyUserService;

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    public InlineQueryCalculatorHandler(DealProcessor dealProcessor, IUpdateService updateService,
                                        ExchangeService exchangeService, IModifyUserService modifyUserService,
                                        ICallbackDataService callbackDataService, IRedisUserStateService redisUserStateService) {
        this.dealProcessor = dealProcessor;
        this.updateService = updateService;
        this.exchangeService = exchangeService;
        this.modifyUserService = modifyUserService;
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Update update) {
        if (dealProcessor.isMainMenuCommand(update)) return;
        Long chatId = UpdateType.getChatId(update);
        if (update.hasCallbackQuery() && callbackDataService.isCallbackQueryData(CallbackQueryData.BACK, update.getCallbackQuery().getData())) {
            modifyUserService.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
            redisUserStateService.delete(chatId);
            dealProcessor.run(update);
            return;
        }
        if (update.hasMessage()) {
            if (!exchangeService.calculateDealAmount(chatId, updateService.getBigDecimalFromText(update))) return;
            modifyUserService.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
            redisUserStateService.delete(chatId);
            dealProcessor.run(update);
        } else if (update.hasInlineQuery()) exchangeService.calculateForInlineQuery(update);
    }

    @Override
    public UserState getUserState() {
        return UserState.INLINE_QUERY_CALCULATOR;
    }
}
