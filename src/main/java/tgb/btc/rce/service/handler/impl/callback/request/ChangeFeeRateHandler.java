package tgb.btc.rce.service.handler.impl.callback.request;

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
public class ChangeFeeRateHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IKeyboardService keyboardService;

    public ChangeFeeRateHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                IRedisUserStateService redisUserStateService, IRedisStringService redisStringService,
                                IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Integer messageId = callbackDataService.getIntArgument(callbackQuery.getData(), 2);
        redisStringService.save(RedisPrefix.DEAL_PID, chatId, dealPid.toString());
        redisStringService.save(RedisPrefix.MESSAGE_ID, chatId, messageId.toString());
        redisUserStateService.save(chatId, UserState.NEW_FEE_RATE);
        responseSender.sendMessage(chatId, "Введите новое целочисленное значение комиссии в <b>sat/vB</b>.", keyboardService.getReplyCancel());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CHANGE_FEE_RATE;
    }
}
