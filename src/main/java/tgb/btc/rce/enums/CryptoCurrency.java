package tgb.btc.rce.enums;

import tgb.btc.rce.exception.EnumTypeNotFoundException;

import java.util.Arrays;

public enum CryptoCurrency {
    BITCOIN("Bitcoin", "btc"),
    LITECOIN("Litecoin", "ltc"),
    TETHER("Tether", "usdt");

    final String displayName;
    final String shortName;

    CryptoCurrency(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
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
