package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.AutoConfirmConfig;
import tgb.btc.library.bean.bot.MerchantConfig;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DeliveryType;
import tgb.btc.library.constants.enums.web.merchant.AutoConfirmType;
import tgb.btc.library.interfaces.service.bean.bot.IAutoConfirmConfigService;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantConfigService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Optional;

@Service
public class AutoConfirmHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IBotMerchantService botMerchantService;

    private final IMerchantConfigService merchantConfigService;

    private final IAutoConfirmConfigService autoConfirmConfigService;

    public AutoConfirmHandler(ICallbackDataService callbackDataService, IBotMerchantService botMerchantService,
                              IMerchantConfigService merchantConfigService, IAutoConfirmConfigService autoConfirmConfigService) {
        this.callbackDataService = callbackDataService;
        this.botMerchantService = botMerchantService;
        this.merchantConfigService = merchantConfigService;
        this.autoConfirmConfigService = autoConfirmConfigService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        CryptoCurrency cryptoCurrency = CryptoCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2));
        DeliveryType deliveryType = DeliveryType.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 3));
        AutoConfirmType autoConfirmType = AutoConfirmType.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 4));

        MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
        Optional<AutoConfirmConfig> autoConfirmConfigOptional =
                merchantConfig.getAutoConfirmConfig(cryptoCurrency, deliveryType, autoConfirmType);
        if (autoConfirmConfigOptional.isPresent()) {
            AutoConfirmConfig autoConfirmConfig = autoConfirmConfigOptional.get();
            merchantConfig.getConfirmConfigs().remove(autoConfirmConfig);
            merchantConfigService.save(merchantConfig);
            autoConfirmConfigService.delete(autoConfirmConfig);
        } else {
            AutoConfirmConfig autoConfirmConfig = autoConfirmConfigService.create(autoConfirmType, cryptoCurrency, deliveryType);
            merchantConfig.getConfirmConfigs().removeIf(conf -> conf.getCryptoCurrency().equals(cryptoCurrency) && conf.getDeliveryType().equals(deliveryType));
            merchantConfig.getConfirmConfigs().add(autoConfirmConfig);
            merchantConfigService.save(merchantConfig);
        }
        botMerchantService.sendMerchantAutoConfirms(merchant, callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.AUTO_CONFIRM;
    }
}
