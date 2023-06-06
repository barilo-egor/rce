package tgb.btc.lib.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.CryptoCurrency;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.CallbackQueryUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.TurningCurrenciesUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

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
