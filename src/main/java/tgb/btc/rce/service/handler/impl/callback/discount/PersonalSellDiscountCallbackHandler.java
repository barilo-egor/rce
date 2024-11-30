package tgb.btc.rce.service.handler.impl.callback.discount;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class PersonalSellDiscountCallbackHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    public PersonalSellDiscountCallbackHandler(ICallbackDataService callbackDataService,
                                               IRedisUserStateService redisUserStateService,
                                               IRedisStringService redisStringService, IResponseSender responseSender,
                                               IKeyboardService keyboardService) {
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long userChatId = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        redisStringService.save(chatId, userChatId.toString());
        redisUserStateService.save(chatId, UserState.NEW_PERSONAL_SELL);
        responseSender.sendMessage(chatId, "Введите новое значение персональной скидки на продажу: " +
                "<b>положительное</b> для скидки, <b>отрицательное</b> для надбавки.", keyboardService.getReplyCancel());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PERSONAL_SELL_DISCOUNT;
    }
}
