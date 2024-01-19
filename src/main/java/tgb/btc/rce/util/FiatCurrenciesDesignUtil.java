package tgb.btc.rce.util;

import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.exception.BaseException;

public class FiatCurrenciesDesignUtil {
    public static String getDisplayData(FiatCurrency currency) {
        return PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString(currency.name());
    }

    public static FiatCurrency fromDisplayData(String displayData) {
        return FiatCurrency.valueOf(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getKeys().stream()
                .filter(key -> displayData.equals(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString(key)))
                .findFirst()
                .orElseThrow(() -> new BaseException("Фиатная валюта по значению " + displayData + " не найдена")));
    }
}
