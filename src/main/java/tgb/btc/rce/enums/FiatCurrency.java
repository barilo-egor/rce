package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

public enum FiatCurrency {
    /**
     * Бел.рубль
     */
    BYN("byn", "бел.рублей"),
    /**
     * Рос.рубль
     */
    RUB("rub", "₽"),
    UAD("uah", "гривен");

    final String code;

    final String displayName;

    FiatCurrency(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public static FiatCurrency getByCode(String code) {
        for (FiatCurrency fiatCurrency : FiatCurrency.values()) {
            if (fiatCurrency.getCode().equals(code)) return fiatCurrency;
        }
        throw new BaseException("Фиатная валюта не найдена.");
    }
}
