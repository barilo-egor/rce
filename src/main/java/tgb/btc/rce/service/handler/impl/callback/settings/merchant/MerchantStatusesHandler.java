package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class MerchantStatusesHandler implements ICallbackQueryHandler {

    private final IBotMerchantService botMerchantService;

    private final ICallbackDataService callbackDataService;

    public MerchantStatusesHandler(IBotMerchantService botMerchantService, ICallbackDataService callbackDataService) {
        this.botMerchantService = botMerchantService;
        this.callbackDataService = callbackDataService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        botMerchantService.sendMerchantStatuses(callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1)));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANT_STATUSES;
    }
}
