package tgb.btc.rce.service.handler.impl.state.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.impl.callback.request.pool.BitcoinPoolWithdrawalHandler;
import tgb.btc.rce.service.handler.impl.message.text.command.request.RequestsHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class NewPoolFeeRateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final BitcoinPoolWithdrawalHandler bitcoinPoolWithdrawalHandler;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final RequestsHandler requestsHandler;

    public NewPoolFeeRateHandler(IResponseSender responseSender, IRedisStringService redisStringService,
                                 IRedisUserStateService redisUserStateService,
                                 BitcoinPoolWithdrawalHandler bitcoinPoolWithdrawalHandler,
                                 ICryptoWithdrawalService cryptoWithdrawalService, RequestsHandler requestsHandler) {
        this.responseSender = responseSender;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.bitcoinPoolWithdrawalHandler = bitcoinPoolWithdrawalHandler;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.requestsHandler = requestsHandler;
    }

    @Override
    public void handle(Update update) {
        Long chatId = UpdateType.getChatId(update);
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(chatId, "Введите новое значение для комиссии, либо нажмите \""
                    + BotReplyButton.CANCEL.getText() + "\".");
            return;
        }
        Long dealsSize = Long.parseLong(redisStringService.get(RedisPrefix.DEALS_SIZE, chatId));
        String totalAmount = redisStringService.get(RedisPrefix.TOTAL_AMOUNT, chatId);
        Integer messageId = Integer.parseInt(redisStringService.get(RedisPrefix.MESSAGE_ID, chatId));
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(BotReplyButton.CANCEL.getText())) {
            redisStringService.delete(RedisPrefix.DEAL_PID, chatId);
            redisStringService.delete(RedisPrefix.TOTAL_AMOUNT, chatId);
            redisStringService.delete(RedisPrefix.MESSAGE_ID, chatId);
            redisUserStateService.delete(chatId);
            bitcoinPoolWithdrawalHandler.sendConfirmMessage(chatId, dealsSize, totalAmount, messageId);
            return;
        }
        String newFeeRate;
        if (update.getMessage().getText().equals(cryptoWithdrawalService.getAutoName())) {
            cryptoWithdrawalService.putAutoFeeRate(CryptoCurrency.BITCOIN);
        } else {
            try {
                newFeeRate = String.valueOf(Integer.parseInt(update.getMessage().getText()));
            } catch (NumberFormatException e) {
                responseSender.sendMessage(chatId, "Требуется целочисленное значение для новой комиссии.");
                return;
            }
            cryptoWithdrawalService.putFeeRate(CryptoCurrency.BITCOIN, newFeeRate);
        }
        redisStringService.delete(RedisPrefix.DEAL_PID, chatId);
        redisStringService.delete(RedisPrefix.MESSAGE_ID, chatId);
        redisUserStateService.delete(chatId);
        bitcoinPoolWithdrawalHandler.sendConfirmMessage(chatId, dealsSize, totalAmount, messageId);
        requestsHandler.handle(update.getMessage());
    }

    @Override
    public UserState getUserState() {
        return UserState.NEW_POOL_FEE_RATE;
    }
}
