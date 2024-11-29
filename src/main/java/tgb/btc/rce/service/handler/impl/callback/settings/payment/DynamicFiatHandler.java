package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.ITurnDynamicRequisiteService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DynamicFiatHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final ITurnDynamicRequisiteService turnDynamicRequisiteService;

    public DynamicFiatHandler(ICallbackDataService callbackDataService,
                              ITurnDynamicRequisiteService turnDynamicRequisiteService) {
        this.callbackDataService = callbackDataService;
        this.turnDynamicRequisiteService = turnDynamicRequisiteService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        turnDynamicRequisiteService.sendPaymentTypes(chatId, callbackQuery.getMessage().getMessageId(), fiatCurrency);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DYNAMIC_FIAT;
    }
}
