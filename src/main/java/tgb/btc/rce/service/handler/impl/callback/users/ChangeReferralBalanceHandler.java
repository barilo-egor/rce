package tgb.btc.rce.service.handler.impl.callback.users;

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
public class ChangeReferralBalanceHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    private final IRedisUserStateService redisUserStateService;

    private final ICallbackDataService callbackDataService;

    private final IRedisStringService redisStringService;

    public ChangeReferralBalanceHandler(IResponseSender responseSender, IKeyboardService keyboardService,
                                        IRedisUserStateService redisUserStateService,
                                        ICallbackDataService callbackDataService, IRedisStringService redisStringService) {
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
        this.redisUserStateService = redisUserStateService;
        this.callbackDataService = callbackDataService;
        this.redisStringService = redisStringService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long userChatId = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Long chatId = callbackQuery.getFrom().getId();
        redisStringService.save(chatId, userChatId.toString());
        responseSender.sendMessage(chatId, """
                        Введите новую сумму для пользователя.
                        Для того, чтобы полностью заменить значение, отправьте новое число без знаков. Пример:
                        1750
                        
                        Чтобы добавить к текущему значению баланса сумму, отправьте число со знаком "+". Пример:
                        +1750
                        
                        Чтобы отнять от текущего значения баланса сумму, отправьте число со знаком "-". Пример:
                        -1750""",
                keyboardService.getReplyCancel());
        redisUserStateService.save(chatId, UserState.CHANGE_REFERRAL_BALANCE);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CHANGE_REFERRAL_BALANCE;
    }
}
