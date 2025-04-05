package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;

@Service
public class OnlyPaysBindingCallbackHandler implements ICallbackQueryHandler {

    private final IBotMerchantService botMerchantService;

    public OnlyPaysBindingCallbackHandler(IBotMerchantService botMerchantService) {
        this.botMerchantService = botMerchantService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        botMerchantService.sendRequestMethod(Merchant.ONLY_PAYS, callbackQuery.getMessage().getChatId(),
                callbackQuery.getData(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ONLY_PAYS_BINDING;
    }
}
