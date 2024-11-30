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
public class ChangeMinSumHandler implements ITextCommandHandler {

    private final IFiatCurrencyService fiatCurrencyService;

    private final IKeyboardService keyboardService;

    private final IResponseSender responseSender;

    public ChangeMinSumHandler(IFiatCurrencyService fiatCurrencyService, IKeyboardService keyboardService,
                               IResponseSender responseSender) {
        this.fiatCurrencyService = fiatCurrencyService;
        this.keyboardService = keyboardService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (fiatCurrencyService.isFew()) {
            responseSender.sendMessage(chatId, "Выберите фиатную валюту для изменения минимальной суммы.",
                    keyboardService.getInlineFiats(CallbackQueryData.FIAT_CHANGE_MIN_SUM));
        } else {
            responseSender.sendMessage(chatId, "Выберите тип сделки для изменения минимальной суммы.",
                    keyboardService.getInlineDealTypes(CallbackQueryData.DEAL_TYPE_CHANGE_MIN_SUM,
                            fiatCurrencyService.getFirst()));
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.CHANGE_MIN_SUM;
    }
}
