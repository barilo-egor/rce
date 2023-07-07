package tgb.btc.rce.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.util.CallbackQueryUtil;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FiatCurrency {
    /**
     * Бел.рубль
     */
    BYN("byn", "Бел.рубли", "бел.рублей", "\uD83C\uDDE7\uD83C\uDDFE"),
    /**
     * Рос.рубль
     */
    RUB("rub", "Рос.рубли","₽", "\uD83C\uDDF7\uD83C\uDDFA"),
    UAH("uah", "Гривны", "гривен", "\uD83C\uDDFA\uD83C\uDDE6");

    final String code;

    final String displayName;

    final String genitive;

    final String flag;

    FiatCurrency(String code, String displayName, String genitive, String flag) {
        this.code = code;
        this.displayName = displayName;
        this.genitive = genitive;
        this.flag = flag;
    }

    public String getName() {
        return this.name();
    }

    public String getDisplayName() {
        return genitive;
    }

    public String getCode() {
        return code;
    }

    public String getFlag() {
        return flag;
    }

    public String getGenitive() {
        return genitive;
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
