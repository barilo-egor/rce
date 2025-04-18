package tgb.btc.rce.service.handler.impl.state.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.MerchantConfig;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantConfigService;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.List;

@Service
public class MerchantDelayStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IBotMerchantService botMerchantService;

    private final IMerchantConfigService merchantConfigService;

    public MerchantDelayStateHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService, IRedisStringService redisStringService, IBotMerchantService botMerchantService, ICallbackDataService callbackDataService, IMerchantConfigService merchantConfigService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.botMerchantService = botMerchantService;
        this.merchantConfigService = merchantConfigService;
    }

    @Override
    public void handle(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals(CallbackQueryData.MERCHANTS_DELAY.name())) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            redisUserStateService.delete(chatId);
            redisStringService.delete(RedisPrefix.MERCHANT, chatId);
            redisStringService.delete(RedisPrefix.MESSAGE_ID, chatId);
            botMerchantService.sendDelay(chatId, update.getCallbackQuery().getMessage().getMessageId());
            return;
        }
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Введите новое значение задержки, либо нажмите кнопку отмены выше.");
            return;
        }
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        int newDelay;
        try {
            newDelay = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Введите целочисленное значение.");
            return;
        }
        Integer messageId = Integer.parseInt(redisStringService.get(RedisPrefix.MESSAGE_ID, chatId));
        String merchantString = redisStringService.get(RedisPrefix.MERCHANT, chatId);
        if ("ALL".equals(merchantString)) {
            List<MerchantConfig> merchantConfigList = merchantConfigService.findAll();
            for (MerchantConfig merchantConfig : merchantConfigList) {
                merchantConfig.setDelay(newDelay);
                merchantConfigService.save(merchantConfig);
            }
        } else {
            Merchant merchant = Merchant.valueOf(merchantString);
            MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
            merchantConfig.setDelay(newDelay);
            merchantConfigService.save(merchantConfig);
        }
        redisUserStateService.delete(chatId);
        redisStringService.delete(RedisPrefix.MERCHANT, chatId);
        redisStringService.delete(RedisPrefix.MESSAGE_ID, chatId);
        responseSender.deleteMessage(chatId, update.getMessage().getMessageId());
        botMerchantService.sendDelay(chatId, messageId);
    }

    @Override
    public UserState getUserState() {
        return UserState.MERCHANT_DELAY;
    }
}
