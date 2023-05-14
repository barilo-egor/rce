package tgb.btc.rce.enums;

public enum FiatCurrency {
    /**
     * Бел.рубль
     */
    BYN("byn"),
    /**
     * Рос.рубль
     */
    RUB("rub");

    final String code;

    FiatCurrency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
