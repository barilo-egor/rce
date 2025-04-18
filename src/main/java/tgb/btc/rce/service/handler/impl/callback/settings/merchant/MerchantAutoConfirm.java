package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class MerchantAutoConfirm implements ICallbackQueryHandler {

    private final IBotMerchantService botMerchantService;

    private final ICallbackDataService callbackDataService;

    public MerchantAutoConfirm(IBotMerchantService botMerchantService, ICallbackDataService callbackDataService) {
        this.botMerchantService = botMerchantService;
        this.callbackDataService = callbackDataService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        botMerchantService.sendMerchantAutoConfirms(merchant, callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANT_AUTO_CONFIRM;
    }
}
