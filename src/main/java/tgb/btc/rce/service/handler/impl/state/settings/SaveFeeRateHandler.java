package tgb.btc.rce.service.handler.impl.state.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.impl.callback.request.AutoWithdrawalDealHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReplyButton;

@Service
public class SaveFeeRateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final AutoWithdrawalDealHandler autoWithdrawalDealHandler;

    private final IReadDealService readDealService;

    public SaveFeeRateHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                              IRedisUserStateService redisUserStateService, IRedisStringService redisStringService,
                              AutoWithdrawalDealHandler autoWithdrawalDealHandler, IReadDealService readDealService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.autoWithdrawalDealHandler = autoWithdrawalDealHandler;
        this.readDealService = readDealService;
    }

    @Override
    public void handle(Update update) {
        Long chatId = UpdateType.getChatId(update);
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(chatId, "Введите новое значение для комиссии, либо нажмите \""
                    + BotReplyButton.CANCEL.getText() + "\".");
            return;
        }
        Long dealPid = Long.parseLong(redisStringService.get(RedisPrefix.DEAL_PID, chatId));
        Integer messageId = Integer.parseInt(redisStringService.get(RedisPrefix.MESSAGE_ID, chatId));
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(BotReplyButton.CANCEL.getText())) {
            redisStringService.delete(RedisPrefix.DEAL_PID, chatId);
            redisStringService.delete(RedisPrefix.MESSAGE_ID, chatId);
            redisUserStateService.delete(chatId);
            autoWithdrawalDealHandler.sendConfirmMessage(true, readDealService.findByPid(dealPid), chatId, messageId);
            return;
        }
        int newFeeRate;
        try {
            newFeeRate = Integer.parseInt(update.getMessage().getText());
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Требуется целочисленное значение для новой комиссии.");
            return;
        }
        autoWithdrawalDealHandler.setLastFeeRate(Integer.toString(newFeeRate));
        redisStringService.delete(RedisPrefix.DEAL_PID, chatId);
        redisStringService.delete(RedisPrefix.MESSAGE_ID, chatId);
        redisUserStateService.delete(chatId);
        autoWithdrawalDealHandler.sendConfirmMessage(true, readDealService.findByPid(dealPid), chatId, messageId);
    }

    @Override
    public UserState getUserState() {
        return UserState.NEW_FEE_RATE;
    }
}
