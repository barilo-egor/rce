package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.TurningCurrenciesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.TURNING_CURRENCY)
@Slf4j
public class TurningCurrencyProcessor extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
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
                KeyboardUtil.buildInline(buttons, 2));
    }

    private InlineButton buildButton(CryptoCurrency currency, DealType dealType) {
        boolean isCurrencyOn = TurningCurrenciesUtil.getIsOn(currency, dealType);
        Command command = isCurrencyOn ? Command.TURN_OFF_CURRENCY : Command.TURN_ON_CURRENCY;
        return InlineButton.builder()
                .text(isCurrencyOn ? "Выключить " + currency.getShortName() : "Включить " + currency.getShortName())
                .data(CallbackQueryUtil.buildCallbackData(command.getText(), dealType.name(), currency.name()))
                .build();
    }
}
