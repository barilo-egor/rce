package tgb.btc.rce.util;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.rce.enums.properties.BotProperties;

public final class FunctionPropertiesUtil {
    private FunctionPropertiesUtil() {
    }

    public static Boolean getSumToReceive(CryptoCurrency cryptoCurrency) {
        return BotProperties.FUNCTIONS.getBoolean("sum.to.receive." + cryptoCurrency.getShortName());
    }
}
