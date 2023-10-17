package tgb.btc.rce.web.util;

import tgb.btc.library.constants.enums.system.DesignProperties;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.exception.BaseException;

public class CryptoCurrenciesDesignUtil {

    public static String getDisplayName(CryptoCurrency currency) {
        return DesignProperties.CRYPTO_CURRENCIES_DESIGN.getString(currency.name());
    }

    public static CryptoCurrency fromDisplayName(String displayName) {
        return CryptoCurrency.valueOf(DesignProperties.CRYPTO_CURRENCIES_DESIGN.getKeys().stream()
                .filter(key -> displayName.equals(DesignProperties.CRYPTO_CURRENCIES_DESIGN.getString(key)))
                .findFirst()
                .orElseThrow(() -> new BaseException("Криптовалюта по значению " + displayName + " не найдена")));
    }
}
