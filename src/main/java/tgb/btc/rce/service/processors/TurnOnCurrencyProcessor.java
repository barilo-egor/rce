package tgb.btc.rce.service.processors;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.TurningCurrenciesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.TURN_ON_CURRENCY)
public class TurnOnCurrencyProcessor extends Processor {

    private TurningCurrencyProcessor turningCurrencyProcessor;

    @Autowired
    public void setTurningCurrencyProcessor(TurningCurrencyProcessor turningCurrencyProcessor) {
        this.turningCurrencyProcessor = turningCurrencyProcessor;
    }

    @Autowired
    public TurnOnCurrencyProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        DealType dealType = DealType.valueOf(values[1]);
        CryptoCurrency currency = CryptoCurrency.valueOf(values[2]);

        if (DealType.BUY.equals(dealType)) {
            TurningCurrenciesUtil.BUY_TURNING.put(currency, true);
            TurningCurrenciesUtil.TURNING_PROPERTIES.setProperty("buy." + currency.name(), true);
        } else {
            TurningCurrenciesUtil.SELL_TURNING.put(currency, true);
            TurningCurrenciesUtil.TURNING_PROPERTIES.setProperty("sell." + currency.name(), true);
        }
        try {
            TurningCurrenciesUtil.TURNING_PROPERTIES.save();
        } catch (ConfigurationException e) {
            responseSender.sendMessage(UpdateUtil.getChatId(update), "Ошибка: " + e.getMessage() + "\n"
            + ExceptionUtils.getFullStackTrace(e));
            return;
        }
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        turningCurrencyProcessor.run(update);
    }
}
