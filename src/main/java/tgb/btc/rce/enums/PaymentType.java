package tgb.btc.rce.enums;

public enum PaymentType {
    RF_CARD("Карта РФ"),
    QIWI("QIWI"),
    TINKOFF("Tinkoff");

    final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
