package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class TurnPaymentTypesHandler implements ITextCommandHandler {

    private final IFiatCurrencyService fiatCurrencyService;

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    public TurnPaymentTypesHandler(IFiatCurrencyService fiatCurrencyService, IResponseSender responseSender,
                                   IKeyboardService keyboardService) {
        this.fiatCurrencyService = fiatCurrencyService;
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (fiatCurrencyService.isFew()) {
            responseSender.sendMessage(chatId, "Выберите фиатную валюту для включения/выключения типа оплаты.",
                    keyboardService.getInlineFiats(CallbackQueryData.FIAT_TURN_PAYMENT_TYPES));
        } else {
            FiatCurrency fiatCurrency = fiatCurrencyService.getFirst();
            responseSender.sendMessage(chatId, "Выберите тип сделки для включения/выключения <b>" + fiatCurrency.getDisplayName()
                    + "</b> типа оплаты.", keyboardService.getInlineDealTypes(CallbackQueryData.DEAL_TYPE_TURN_PAYMENT_TYPES,
                    fiatCurrencyService.getFirst()));
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.TURN_PAYMENT_TYPES;
    }
}
