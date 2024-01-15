package tgb.btc.rce.util;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.exception.BaseException;

public class CryptoCurrenciesDesignUtil {

    public static String getDisplayName(CryptoCurrency currency) {
        return PropertiesPath.CRYPTO_CURRENCIES_DESIGN_PROPERTIES.getString(currency.name());
    }

    public static CryptoCurrency fromDisplayName(String displayName) {
        return CryptoCurrency.valueOf(PropertiesPath.CRYPTO_CURRENCIES_DESIGN_PROPERTIES.getKeys().stream()
                .filter(key -> displayName.equals(PropertiesPath.CRYPTO_CURRENCIES_DESIGN_PROPERTIES.getString(key)))
                .findFirst()
                .orElseThrow(() -> new BaseException("Криптовалюта по значению " + displayName + " не найдена")));
    }
}
