package tgb.btc.rce.util;

import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.FiatCurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FiatCurrenciesUtil {
    private FiatCurrenciesUtil() {
    }

    private static final List<FiatCurrency> FIAT_CURRENCIES;

    static {
        FIAT_CURRENCIES = Arrays.stream(BotProperties.BOT_CONFIG_PROPERTIES.getStringArray("bot.fiat.currencies"))
                .map(FiatCurrency::valueOf)
                .collect(Collectors.toList());
    }

    public static List<FiatCurrency> getFiatCurrencies() {
        return new ArrayList<>(FIAT_CURRENCIES);
    }
}
