package tgb.btc.rce.service.handler.impl.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
public class ShareReviewHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IKeyboardService keyboardService;

    private final ICallbackDataService callbackDataService;

    public ShareReviewHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
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
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.sendMessage(chatId, "Напишите ваш отзыв.", keyboardService.getReplyCancel());
        redisStringService.save(RedisPrefix.MESSAGE_ID, chatId, messageId.toString());
        redisStringService.save(RedisPrefix.DEAL_PID, chatId,
                callbackDataService.getLongArgument(callbackQuery.getData(), 1).toString());
        redisUserStateService.save(chatId, UserState.SHARE_REVIEW);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SHARE_REVIEW;
    }
}
