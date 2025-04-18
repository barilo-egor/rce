package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;

@Service
public class MerchantBindingHandler implements ICallbackQueryHandler {

    private final IBotMerchantService botMerchantService;

    public MerchantBindingHandler(IBotMerchantService botMerchantService) {
        this.botMerchantService = botMerchantService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        botMerchantService.sendRequestPaymentType(callbackQuery);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANT_BINDING;
    }
}
