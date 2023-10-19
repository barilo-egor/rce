package tgb.btc.rce.util;

import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.DesignProperties;

public class FiatCurrenciesDesignUtil {
    public static String getDisplayData(FiatCurrency currency) {
        return DesignProperties.BUTTONS_DESIGN.getString(currency.name());
    }

    public static FiatCurrency fromDisplayData(String displayData) {
        return FiatCurrency.valueOf(DesignProperties.BUTTONS_DESIGN.getKeys().stream()
                .filter(key -> displayData.equals(DesignProperties.BUTTONS_DESIGN.getString(key)))
                .findFirst()
                .orElseThrow(() -> new BaseException("Фиатная валюта по значению " + displayData + " не найдена")));
    }
}
