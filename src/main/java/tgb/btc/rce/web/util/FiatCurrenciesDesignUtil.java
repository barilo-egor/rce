package tgb.btc.rce.web.util;

import tgb.btc.rce.enums.DesignProperties;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.BaseException;

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
