package tgb.btc.rce.enums;

import tgb.btc.rce.exception.EnumTypeNotFoundException;

import java.util.Arrays;

public enum CryptoCurrency {
    BITCOIN("Bitcoin", "btc", Double.class, 8),
    LITECOIN("Litecoin", "ltc", Double.class, 4),
    USDT("USDT(trc20)", "usdt", String.class, 1);

    final String displayName;
    final String shortName;
    final Class rateClass;
    final int scale;

    CryptoCurrency(String displayName, String shortName, Class rateClass, int scale) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.rateClass = rateClass;
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }

    public Class getRateClass() {
        return rateClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public static CryptoCurrency fromShortName(String shortName) {
        return Arrays.stream(CryptoCurrency.values()).filter(t -> t.getShortName().equals(shortName)).findFirst()
                .orElseThrow(() -> new EnumTypeNotFoundException("Не найдена крипто валюта: " + shortName));
    }
}
