package tgb.btc.rce.service.handler.impl.callback.discount;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class ReferralPercentCallbackHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    public ReferralPercentCallbackHandler(ICallbackDataService callbackDataService, IResponseSender responseSender,
                                          IRedisUserStateService redisUserStateService,
                                          IRedisStringService redisStringService) {
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long userChatId = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        redisUserStateService.save(chatId, UserState.NEW_REFERRAL_PERCENT);
        redisStringService.save(chatId, userChatId.toString());
        responseSender.sendMessage(chatId, "Введите новый процент реферальных отчислений для пользователя <b>"
                + userChatId + "</b>. Для того чтобы у пользователя был общий процент, введите 0.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.REFERRAL_PERCENT;
    }
}
