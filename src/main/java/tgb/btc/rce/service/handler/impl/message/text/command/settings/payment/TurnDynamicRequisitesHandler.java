package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class TurnDynamicRequisitesHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IFiatCurrencyService fiatCurrencyService;

    public TurnDynamicRequisitesHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                        IFiatCurrencyService fiatCurrencyService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Override
    public void handle(Message message) {
        List<FiatCurrency> fiatCurrencies = fiatCurrencyService.getFiatCurrencies();
        List<InlineButton> inlineButtons = fiatCurrencies.stream()
                .map(cur -> InlineButton.builder()
                        .text(cur.getDisplayName())
                        .data(callbackDataService.buildData(CallbackQueryData.DYNAMIC_FIAT, cur.name()))
                        .build()
                )
                .toList();
        responseSender.sendMessage(
                message.getChatId(),
                "Выберите фиатную валюту для включения/выключения динамических реквизитов.",
                inlineButtons
        );
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.TURN_DYNAMIC_REQUISITES;
    }
}
