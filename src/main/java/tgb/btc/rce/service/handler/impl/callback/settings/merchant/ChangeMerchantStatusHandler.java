package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.MerchantConfig;
import tgb.btc.library.bean.bot.MerchantSuccessStatus;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantConfigService;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantSuccessStatusService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Optional;

@Service
public class ChangeMerchantStatusHandler implements ICallbackQueryHandler {

    private final IBotMerchantService botMerchantService;

    private final ICallbackDataService callbackDataService;

    private final IMerchantConfigService merchantConfigService;

    private final IMerchantSuccessStatusService merchantSuccessStatusService;

    public ChangeMerchantStatusHandler(IBotMerchantService botMerchantService, ICallbackDataService callbackDataService,
                                       IMerchantConfigService merchantConfigService, IMerchantSuccessStatusService merchantSuccessStatusService) {
        this.botMerchantService = botMerchantService;
        this.callbackDataService = callbackDataService;
        this.merchantConfigService = merchantConfigService;
        this.merchantSuccessStatusService = merchantSuccessStatusService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        String statusName = callbackDataService.getArgument(callbackQuery.getData(), 2);
        MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
        Optional<MerchantSuccessStatus> optional = merchantConfig.getSuccessStatuses().stream()
                .filter(st -> st.getStatus().equals(statusName))
                .findFirst();
        if (optional.isPresent()) {
            MerchantSuccessStatus status = optional.get();
            merchantConfig.getSuccessStatuses().remove(status);
            merchantConfigService.save(merchantConfig);
            merchantSuccessStatusService.delete(status);
        } else {
            merchantConfig.getSuccessStatuses().add(merchantSuccessStatusService.create(statusName));
            merchantConfigService.save(merchantConfig);
        }
        botMerchantService.sendMerchantStatuses(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId(), merchant);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CHANGE_MERCHANT_STATUS;
    }
}
