package tgb.btc.rce.util;

import org.apache.commons.collections4.CollectionUtils;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.exception.BaseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FiatCurrencyUtil {
    private FiatCurrencyUtil() {
    }

    private static final List<FiatCurrency> FIAT_CURRENCIES;

    private static final boolean IS_FEW;

    static {
        FIAT_CURRENCIES = Arrays.stream(BotProperties.BOT_CONFIG_PROPERTIES.getStringArray("bot.fiat.currencies"))
                .map(FiatCurrency::valueOf)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(FIAT_CURRENCIES)) throw new BaseException("Не найдена ни одна фиатная валюта");
        IS_FEW = FIAT_CURRENCIES.size() > 1;
    }

    public static List<FiatCurrency> getFiatCurrencies() {
        return new ArrayList<>(FIAT_CURRENCIES);
    }

    public static boolean isFew() {
        return IS_FEW;
    }

    public static FiatCurrency getFirst() {
        return FIAT_CURRENCIES.get(0);
    }
}
