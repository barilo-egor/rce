package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.MerchantConfig;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantConfigService;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class MerchantMaxAmountHandler implements ICallbackQueryHandler {

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IMerchantConfigService merchantConfigService;

    public MerchantMaxAmountHandler(IRedisUserStateService redisUserStateService, IRedisStringService redisStringService,
                                    IResponseSender responseSender, ICallbackDataService callbackDataService,
                                    IMerchantConfigService merchantConfigService) {
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.merchantConfigService = merchantConfigService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
        redisUserStateService.save(chatId, UserState.MERCHANT_MAX_AMOUNT);
        redisStringService.save(RedisPrefix.MERCHANT, chatId, merchant.name());
        redisStringService.save(RedisPrefix.MESSAGE_ID, chatId, callbackQuery.getMessage().getMessageId().toString());
        responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                "Текущее значение максимальной суммы мерчанта <b>" + merchant.getDisplayName() + "</b>: <code>" + merchantConfig.getMaxAmount()
                        + "</code>.\nВведите новое значение.",
                List.of(InlineButton.builder().text("Отмена").data(CallbackQueryData.MERCHANTS_MAX_AMOUNTS.name()).build()));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANT_MAX_AMOUNT;
    }
}
