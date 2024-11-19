package tgb.btc.rce.service.handler.util.impl.handler;

import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.ITurningCurrencyService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.ITurningCurrenciesService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class TurningCurrencyService implements ITurningCurrencyService {

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    private final ITurningCurrenciesService turningCurrenciesService;

    private final ICallbackQueryService callbackQueryService;

    public TurningCurrencyService(IResponseSender responseSender, IKeyboardBuildService keyboardBuildService,
                                  ITurningCurrenciesService turningCurrenciesService,
                                  ICallbackQueryService callbackQueryService) {
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
        this.turningCurrenciesService = turningCurrenciesService;
        this.callbackQueryService = callbackQueryService;
    }

    @Override
    public void process(Long chatId) {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text("Покупка ⬇️")
                .data("null")
                .build()
        );
        buttons.add(InlineButton.builder()
                .text("Продажа ⬇️")
                .data("null")
                .build()
        );

        for (CryptoCurrency currency : CryptoCurrency.values()) {
            for (DealType dealType : DealType.values()) {
                buttons.add(buildButton(currency, dealType));
            }
        }

        responseSender.sendMessage(chatId, "Включение/выключение криптовалют",
                keyboardBuildService.buildInline(buttons, 2));
    }

    private InlineButton buildButton(CryptoCurrency currency, DealType dealType) {
        boolean isCurrencyOn = turningCurrenciesService.getIsOn(currency, dealType);
        Command command = isCurrencyOn ? Command.TURN_OFF_CURRENCY : Command.TURN_ON_CURRENCY;
        return InlineButton.builder()
                .text(isCurrencyOn ? "Выключить " + currency.getShortName() : "Включить " + currency.getShortName())
                .data(callbackQueryService.buildCallbackData(command, new Object[]{dealType.name(), currency.name()}))
                .build();
    }
}
