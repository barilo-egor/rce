package tgb.btc.rce.enums;

public enum DealType {
    BUY("покупку", "buy"),
    SELL("продажу", "sell");

    final String displayName;
    final String key;

    DealType(String displayName, String key) {
        this.displayName = displayName;
        this.key = key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getKey() {
        return key;
    }

    public static boolean isBuy(DealType dealType) {
        return DealType.BUY.equals(dealType);
    }
}
