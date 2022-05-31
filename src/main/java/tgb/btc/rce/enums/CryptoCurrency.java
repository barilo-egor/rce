package tgb.btc.rce.enums;

public enum CryptoCurrency {
    BITCOIN("Bitcoin"),
    LITECOIN("Litecoin"),
    TETHER("Tether");

    final String displayName;

    CryptoCurrency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
