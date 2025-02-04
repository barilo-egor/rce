package tgb.btc.rce.service.handler.impl.callback.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.service.web.CryptoWithdrawalService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReplyButton;

@Service
public class ChangeFeeRateHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final CryptoWithdrawalService cryptoWithdrawalService;

    public ChangeFeeRateHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                IRedisUserStateService redisUserStateService, IRedisStringService redisStringService,
                                CryptoWithdrawalService cryptoWithdrawalService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Integer messageId = callbackDataService.getIntArgument(callbackQuery.getData(), 2);
        redisStringService.save(RedisPrefix.DEAL_PID, chatId, dealPid.toString());
        redisStringService.save(RedisPrefix.MESSAGE_ID, chatId, messageId.toString());
        redisUserStateService.save(chatId, UserState.NEW_FEE_RATE);

        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Введите новое целочисленное значение комиссии в <b>sat/vB</b>.",
                ReplyButton.builder().text(cryptoWithdrawalService.getAutoName()).build(), BotReplyButton.CANCEL.getButton());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CHANGE_FEE_RATE;
    }
}
