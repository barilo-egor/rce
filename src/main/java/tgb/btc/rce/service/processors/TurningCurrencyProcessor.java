package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandProcessor(command = Command.TURNING_CURRENCY)
@Slf4j
public class TurningCurrencyProcessor extends Processor {

    public static Map<CryptoCurrency, Boolean> BUY_TURNING = new HashMap<>();
    public static Map<CryptoCurrency, Boolean> SELL_TURNING = new HashMap<>();
    public static PropertiesConfiguration TURNING_PROPERTIES;

    static {
        try {
            TURNING_PROPERTIES = new PropertiesConfiguration(FilePaths.CURRENCIES_TURNING);
            for (CryptoCurrency currency : CryptoCurrency.values()) {
                BUY_TURNING.put(currency, TURNING_PROPERTIES.getBoolean("buy." + currency.name()));
                SELL_TURNING.put(currency, TURNING_PROPERTIES.getBoolean("sell." + currency.name()));
            }
        } catch (ConfigurationException e) {
            log.error("Ошибка при прочтении " + FilePaths.CURRENCIES_TURNING);
        }
    }

    @Autowired
    public TurningCurrencyProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

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
            buttons.add(
                    InlineButton.builder()
                            .text(BUY_TURNING.get(currency)
                                    ? "Выключить " + currency.getShortName()
                                    : "Включить " + currency.getShortName())
                            .data(BUY_TURNING.get(currency)
                                    ? Command.TURN_OFF_CURRENCY.getText()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + DealType.BUY.name()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + currency.name()
                                    : Command.TURN_ON_CURRENCY.getText()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + DealType.BUY.name()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + currency.name())
                            .build()
            );
            buttons.add(
                    InlineButton.builder()
                            .text(SELL_TURNING.get(currency)
                                    ? "Выключить " + currency.getShortName()
                                    : "Включить " + currency.getShortName())
                            .data(SELL_TURNING.get(currency)
                                    ? Command.TURN_OFF_CURRENCY.getText()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + DealType.SELL.name()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + currency.name()
                                    : Command.TURN_ON_CURRENCY.getText()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + DealType.SELL.name()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + currency.name())
                            .build()
            );
        }

        responseSender.sendMessage(chatId, "Включение/выключение криптовалют",
                KeyboardUtil.buildInline(buttons, 2));
    }
}
