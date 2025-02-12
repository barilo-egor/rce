package tgb.btc.rce.service.handler.impl.callback.request.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReplyButton;

@Service
public class PoolChangeFeeRateHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IResponseSender responseSender;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    @Autowired
    public PoolChangeFeeRateHandler(ICallbackDataService callbackDataService, IRedisStringService redisStringService,
                                    IRedisUserStateService redisUserStateService, IResponseSender responseSender,
                                    ICryptoWithdrawalService cryptoWithdrawalService) {
        this.callbackDataService = callbackDataService;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.responseSender = responseSender;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealsSize = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        String totalAmount = callbackDataService.getArgument(callbackQuery.getData(), 2);
        Integer messageId = callbackDataService.getIntArgument(callbackQuery.getData(), 3);
        redisStringService.save(RedisPrefix.DEALS_SIZE, chatId, dealsSize.toString());
        redisStringService.save(RedisPrefix.TOTAL_AMOUNT, chatId, totalAmount);
        redisStringService.save(RedisPrefix.MESSAGE_ID, chatId, messageId.toString());
        redisUserStateService.save(chatId, UserState.NEW_POOL_FEE_RATE);

        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Введите новое целочисленное значение комиссии в <b>sat/vB</b>.",
                ReplyButton.builder().text(cryptoWithdrawalService.getAutoName()).build(), BotReplyButton.CANCEL.getButton());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.POOL_CHANGE_FEE_RATE;
    }
}
