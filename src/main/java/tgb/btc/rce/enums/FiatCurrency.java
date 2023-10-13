package tgb.btc.rce.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.interfaces.JsonConvertable;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FiatCurrency implements JsonConvertable {
    /**
     * Бел.рубль
     */
    BYN("byn", "Бел.рубли", "бел.рублей", "\uD83C\uDDE7\uD83C\uDDFE", BotProperties.BUTTONS_DESIGN.getString("BYN")),
    /**
     * Рос.рубль
     */
    RUB("rub", "Рос.рубли", "₽", "\uD83C\uDDF7\uD83C\uDDFA", BotProperties.BUTTONS_DESIGN.getString("RUB")),
    UAH("uah", "Гривны", "гривен", "\uD83C\uDDFA\uD83C\uDDE6", BotProperties.BUTTONS_DESIGN.getString("UAH"));

    final String code;

    final String displayName;

    final String genitive;

    final String flag;

    final String displayData;

    FiatCurrency(String code, String displayName, String genitive, String flag, String displayData) {
        this.code = code;
        this.displayName = displayName;
        this.genitive = genitive;
        this.flag = flag;
        this.displayData = displayData;
    }

    public String getName() {
        return this.name();
    }

    public String getGenitive() {
        return genitive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayData() {
        return displayData;
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

    @Override
    public ObjectNode toJson() {
        return MainWebController.DEFAULT_MAPPER.createObjectNode()
                .put("name", this.name())
                .put("code", this.getCode())
                .put("displayName", this.getDisplayName())
                .put("genitive", this.getGenitive())
                .put("flag", this.getFlag());
    }
}
