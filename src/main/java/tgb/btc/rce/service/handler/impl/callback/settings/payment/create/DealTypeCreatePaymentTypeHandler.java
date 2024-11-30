package tgb.btc.rce.service.handler.impl.callback.settings.payment.create;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DealTypeCreatePaymentTypeHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IKeyboardService keyboardService;

    public DealTypeCreatePaymentTypeHandler(ICallbackDataService callbackDataService,
                                            IResponseSender responseSender, IRedisStringService redisStringService,
                                            IRedisUserStateService redisUserStateService, IKeyboardService keyboardService) {
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.keyboardService = keyboardService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        DealType dealType = DealType.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2));
        Long chatId = callbackQuery.getFrom().getId();
        redisStringService.save(RedisPrefix.DEAL_TYPE, chatId, dealType.name());
        redisStringService.save(RedisPrefix.FIAT_CURRENCY, chatId, fiatCurrency.name());
        redisUserStateService.save(chatId, UserState.CREATE_PAYMENT_TYPE);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Введите название для нового типа оплаты.", keyboardService.getReplyCancel());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DEAL_TYPE_CREATE_PAYMENT_TYPE;
    }
}
