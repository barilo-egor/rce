package tgb.btc.rce.service.processors.admin.settings.paymenttypes.turning;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.handler.util.IShowPaymentTypesService;
import tgb.btc.rce.service.util.ICallbackDataService;

@CommandProcessor(command = Command.TURN_PAYMENT_TYPES, step = 2)
public class ShowPaymentTypesForTurn extends Processor {

    private IPaymentTypeService paymentTypeService;

    private IUserDataService userDataService;

    private IFiatCurrencyService fiatCurrencyService;

    private ICallbackDataService callbackDataService;

    private IShowPaymentTypesService showPaymentTypesService;

    @Autowired
    public void setShowPaymentTypesService(IShowPaymentTypesService showPaymentTypesService) {
        this.showPaymentTypesService = showPaymentTypesService;
    }

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public void run(Update update) {
        if (!hasMessageText(update, BotStringConstants.BUY_OR_SELL)) {
            return;
        }
        Long chatId = updateService.getChatId(update);
        String message = updateService.getMessageText(update);
        DealType dealType;
        if (DealType.BUY.getNominativeFirstLetterToUpper().equals(message)) {
            dealType = DealType.BUY;
        } else if (DealType.SELL.getNominativeFirstLetterToUpper().equals(message)) {
            dealType = DealType.SELL;
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL);
            return;
        }
        FiatCurrency fiatCurrency = fiatCurrencyService.isFew()
                ? userDataService.getFiatCurrencyByChatId(chatId)
                : fiatCurrencyService.getFirst();
        showPaymentTypesService.sendForTurn(chatId, dealType, fiatCurrency);
        processToAdminMainPanel(chatId);
    }
}
