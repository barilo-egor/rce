package tgb.btc.rce.enums;

public enum PaymentType {
    RF_CARD("Карта РФ"),
    QIWI("QIWI"),

    BY_CARD("Карта РБ"),
    UAH_CARD("Карта Украина");

    final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
