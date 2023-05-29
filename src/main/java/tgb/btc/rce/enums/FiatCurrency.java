package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.util.CallbackQueryUtil;

public enum FiatCurrency {
    /**
     * Бел.рубль
     */
    BYN("byn", "бел.рублей"),
    /**
     * Рос.рубль
     */
    RUB("rub", "₽"),
    UAH("uah", "гривен");

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
        throw new EnumTypeNotFoundException("Фиатная валюта не найдена.");
    }

    public static FiatCurrency fromCallbackQuery(CallbackQuery callbackQuery) {
        String enteredCurrency = CallbackQueryUtil.getSplitData(callbackQuery, 1);
        return FiatCurrency.valueOf(enteredCurrency);
    }
}
