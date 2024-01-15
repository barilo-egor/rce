package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.properties.PropertiesPath;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TurningCurrenciesUtil {
    public static Map<CryptoCurrency, Boolean> BUY_TURNING = new HashMap<>();
    public static Map<CryptoCurrency, Boolean> SELL_TURNING = new HashMap<>();

    static {
        for (CryptoCurrency currency : CryptoCurrency.values()) {
            BUY_TURNING.put(currency, PropertiesPath.CURRENCIES_TURNING_PROPERTIES.getBoolean("buy." + currency.name()));
            SELL_TURNING.put(currency, PropertiesPath.CURRENCIES_TURNING_PROPERTIES.getBoolean("sell." + currency.name()));
        }
    }

    public static boolean getIsOn(CryptoCurrency cryptoCurrency, DealType dealType) {
        if (DealType.BUY.equals(dealType)) return BooleanUtils.isTrue(BUY_TURNING.get(cryptoCurrency));
        else return BooleanUtils.isTrue(SELL_TURNING.get(cryptoCurrency));
    }

    public static List<CryptoCurrency> getSwitchedOnByDealType(DealType dealType) {
        return Arrays.stream(CryptoCurrency.values())
                .filter(cryptoCurrency -> DealType.BUY.equals(dealType)
                        ? TurningCurrenciesUtil.BUY_TURNING.get(cryptoCurrency)
                        : TurningCurrenciesUtil.SELL_TURNING.get(cryptoCurrency)).collect(Collectors.toList());
    }
}
