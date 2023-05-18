package tgb.btc.rce.enums;

public enum FiatCurrency {
    /**
     * Бел.рубль
     */
    BYN("byn", "рублей"),
    /**
     * Рос.рубль
     */
    RUB("rub", "₽");

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
}
