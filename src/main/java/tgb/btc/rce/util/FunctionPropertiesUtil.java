package tgb.btc.rce.util;

import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.CryptoCurrency;

public final class FunctionPropertiesUtil {
    private FunctionPropertiesUtil() {
    }

    public static Boolean getSumToReceive(CryptoCurrency cryptoCurrency) {
        return BotProperties.FUNCTIONS.getBoolean("sum.to.receive." + cryptoCurrency.getShortName());
    }
}
