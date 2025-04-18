package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;

@Service
public class MerchantsAutoConfirmHandler implements ICallbackQueryHandler {

    private final IBotMerchantService botMerchantService;

    public MerchantsAutoConfirmHandler(IBotMerchantService botMerchantService) {
        this.botMerchantService = botMerchantService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        botMerchantService.sendAutoConfirmMenu(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANTS_AUTO_CONFIRM;
    }
}
