package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class DeletePaymentTypeHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IFiatCurrencyService fiatCurrencyService;

    private final IKeyboardService keyboardService;

    public DeletePaymentTypeHandler(IResponseSender responseSender, IFiatCurrencyService fiatCurrencyService,
                                    IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.fiatCurrencyService = fiatCurrencyService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (fiatCurrencyService.isFew()) {
            responseSender.sendMessage(chatId, "Выберите фиатную валюту.",
                    keyboardService.getInlineFiats(CallbackQueryData.FIAT_DELETE_PAYMENT_TYPE));
        } else {
            responseSender.sendMessage(chatId, "Выберите тип сделки.",
                    keyboardService.getInlineDealTypes(CallbackQueryData.DEAL_TYPE_DELETE_PAYMENT_TYPE,
                            fiatCurrencyService.getFirst()));
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.DELETE_PAYMENT_TYPE;
    }
}
