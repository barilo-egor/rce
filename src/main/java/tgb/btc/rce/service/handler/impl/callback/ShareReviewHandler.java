package tgb.btc.rce.service.handler.impl.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.impl.util.CallbackDataService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class ShareReviewHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IKeyboardService keyboardService;

    public ShareReviewHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                              IRedisStringService redisStringService, IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.sendMessage(chatId, "Напишите ваш отзыв.", keyboardService.getReplyCancel());
        redisStringService.save(chatId, callbackQuery.getData() + CallbackDataService.SPLITTER
                + callbackQuery.getMessage().getMessageId());
        redisUserStateService.save(chatId, UserState.SHARE_REVIEW);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SHARE_REVIEW;
    }
}
