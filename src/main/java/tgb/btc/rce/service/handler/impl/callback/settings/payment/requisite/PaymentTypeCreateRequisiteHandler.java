package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
public class PaymentTypeCreateRequisiteHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    public PaymentTypeCreateRequisiteHandler(ICallbackDataService callbackDataService,
                                             IRedisStringService redisStringService,
                                             IRedisUserStateService redisUserStateService,
                                             IResponseSender responseSender, IKeyboardService keyboardService) {
        this.callbackDataService = callbackDataService;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 2);
        redisStringService.save(RedisPrefix.FIAT_CURRENCY, chatId, fiatCurrency.name());
        redisStringService.save(RedisPrefix.PAYMENT_TYPE_PID, chatId, paymentTypePid.toString());
        redisUserStateService.save(chatId, UserState.CREATE_REQUISITE);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Введите создаваемый реквизит.", keyboardService.getReplyCancel());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PAYMENT_TYPE_CREATE_REQUISITE;
    }
}
