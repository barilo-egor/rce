package tgb.btc.rce.enums;

public enum DealType {
    BUY("покупку"),
    SELL("продажу");

    final String displayName;

    DealType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}