package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;

@Service
public class ChangeReferralBalanceHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    private final IRedisUserStateService redisUserStateService;

    public ChangeReferralBalanceHandler(IResponseSender responseSender, IKeyboardService keyboardService,
                                        IRedisUserStateService redisUserStateService) {
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
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
