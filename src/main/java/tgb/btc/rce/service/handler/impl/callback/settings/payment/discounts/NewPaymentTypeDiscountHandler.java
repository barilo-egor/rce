package tgb.btc.rce.service.handler.impl.callback.settings.payment.discounts;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
public class NewPaymentTypeDiscountHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IKeyboardService keyboardService;

    private final ICallbackDataService callbackDataService;

    public NewPaymentTypeDiscountHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                         IRedisStringService redisStringService, IKeyboardService keyboardService,
                                         ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.keyboardService = keyboardService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        redisUserStateService.save(chatId, UserState.NEW_PAYMENT_TYPE_DISCOUNT);
        redisStringService.save(RedisPrefix.PAYMENT_TYPE_PID, chatId, callbackDataService.getArgument(callbackQuery.getData(), 1));
        responseSender.sendMessage(chatId, """
                Введите данные новой скидки: процент и сумму через пробел.
                Примеры:
                <b>1 5000</b> - для 1% до 5000 рублей
                <b>1.5 10000</b> - для 1.5% до 10000 рублей""", keyboardService.getReplyCancel());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.NEW_PAYMENT_TYPE_DISCOUNT;
    }
}
