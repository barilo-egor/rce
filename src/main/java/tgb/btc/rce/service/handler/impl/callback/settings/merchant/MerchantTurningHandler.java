package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.MerchantConfig;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantConfigService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Objects;

@Service
public class MerchantTurningHandler implements ICallbackQueryHandler {

    private final IMerchantConfigService merchantConfigService;

    private final IBotMerchantService botMerchantService;

    private final ICallbackDataService callbackDataService;

    public MerchantTurningHandler(IMerchantConfigService merchantConfigService, IBotMerchantService botMerchantService,
                                  ICallbackDataService callbackDataService) {
        this.merchantConfigService = merchantConfigService;
        this.botMerchantService = botMerchantService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
        merchantConfig.setIsOn(Objects.isNull(merchantConfig.getIsOn()) || !merchantConfig.getIsOn());
        merchantConfigService.save(merchantConfig);
        botMerchantService.sendIsOn(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANT_TURNING;
    }
}
