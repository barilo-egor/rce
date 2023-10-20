package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.exception.BaseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class FiatCurrencyUtil {
    private FiatCurrencyUtil() {
    }

    private static final List<FiatCurrency> FIAT_CURRENCIES;

    private static final boolean IS_FEW;

    static {
        FIAT_CURRENCIES = Arrays.stream(CommonProperties.CONFIG.getStringArray("bot.fiat.currencies"))
                .map(FiatCurrency::valueOf)
                .collect(Collectors.toList());
        log.info("Загружено " + FIAT_CURRENCIES.size() + " фиатных валют: "
                + FIAT_CURRENCIES.stream()
                .map(FiatCurrency::getGenitive)
                .collect(Collectors.joining(", ")));
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
