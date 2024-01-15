package tgb.btc.rce.util;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;

public final class FunctionPropertiesUtil {
    private FunctionPropertiesUtil() {
    }

    public static Boolean getSumToReceive(CryptoCurrency cryptoCurrency) {
        return PropertiesPath.FUNCTIONS_PROPERTIES.getBoolean("sum.to.receive." + cryptoCurrency.getShortName());
    }
}
