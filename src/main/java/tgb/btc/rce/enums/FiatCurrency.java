package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.util.CallbackQueryUtil;

public enum FiatCurrency {
    /**
     * Бел.рубль
     */
    BYN("byn", "бел.рублей", "\uD83C\uDDE7\uD83C\uDDFE"),
    /**
     * Рос.рубль
     */
    RUB("rub", "₽", "\uD83C\uDDF7\uD83C\uDDFA"),
    UAH("uah", "гривен", "\uD83C\uDDFA\uD83C\uDDE6");

    final String code;

    final String displayName;

    final String flag;

    FiatCurrency(String code, String displayName, String flag) {
        this.code = code;
        this.displayName = displayName;
        this.flag = flag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public String getFlag() {
        return flag;
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
