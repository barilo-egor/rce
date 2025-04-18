package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.MerchantConfig;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.service.bean.bot.MerchantConfigService;
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
public class MerchantAttemptsCountHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final MerchantConfigService merchantConfigService;

    private final IResponseSender responseSender;

    public MerchantAttemptsCountHandler(ICallbackDataService callbackDataService, IRedisStringService redisStringService,
                                        IRedisUserStateService redisUserStateService, MerchantConfigService merchantConfigService,
                                        IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.merchantConfigService = merchantConfigService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String merchantString = callbackDataService.getArgument(callbackQuery.getData(), 1);
        redisUserStateService.save(chatId, UserState.MERCHANT_ATTEMPTS_COUNT);
        redisStringService.save(RedisPrefix.MERCHANT, chatId, merchantString);
        redisStringService.save(RedisPrefix.MESSAGE_ID, chatId, callbackQuery.getMessage().getMessageId().toString());
        List<InlineButton> buttons = List.of(InlineButton.builder().text("Отмена").data(CallbackQueryData.MERCHANTS_ATTEMPTS_COUNT.name()).build());
        if ("ALL".equals(merchantString)) {
            responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                    "Введите новое значение количества попыток для всех мерчантов.", buttons);
        } else {
            Merchant merchant = Merchant.valueOf(merchantString);
            MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
            responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                    "Текущее значение количества попыток мерчанта <b>" + merchant.getDisplayName() + "</b>: <code>" + merchantConfig.getDelay()
                            + "</code>.\nВведите новое значение.", buttons);
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANT_ATTEMPTS_COUNT;
    }
}
