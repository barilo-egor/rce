package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class ChangeMinSumCallbackHandler implements ICallbackQueryHandler {

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IPaymentTypeService paymentTypeService;

    private final IKeyboardService keyboardService;

    public ChangeMinSumCallbackHandler(IRedisUserStateService redisUserStateService,
                                       IRedisStringService redisStringService,
                                       ICallbackDataService callbackDataService, IResponseSender responseSender,
                                       IPaymentTypeService paymentTypeService, IKeyboardService keyboardService) {
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.paymentTypeService = paymentTypeService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        redisStringService.save(RedisPrefix.PAYMENT_TYPE_PID, chatId, paymentTypePid.toString());
        redisUserStateService.save(chatId, UserState.CHANGE_MIN_SUM);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Введите новую минимальную сумму для <b>" + paymentType.getName() + "</b>.",
                keyboardService.getReplyCancel());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CHANGE_MIN_SUM;
    }
}
