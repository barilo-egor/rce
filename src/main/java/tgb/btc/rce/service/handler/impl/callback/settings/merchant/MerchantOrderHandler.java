package tgb.btc.rce.service.handler.impl.callback.settings.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantConfigService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class MerchantOrderHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IMerchantConfigService merchantConfigService;

    private final IBotMerchantService botMerchantService;

    public MerchantOrderHandler(ICallbackDataService callbackDataService, IMerchantConfigService merchantConfigService,
                                IBotMerchantService botMerchantService) {
        this.callbackDataService = callbackDataService;
        this.merchantConfigService = merchantConfigService;
        this.botMerchantService = botMerchantService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        boolean isUp = callbackDataService.getBoolArgument(callbackQuery.getData(), 2);
        merchantConfigService.changeOrder(merchant, isUp);
        botMerchantService.sendOrder(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MERCHANT_ORDER;
    }
}
