package tgb.btc.rce.enums;

public enum PropertiesMessage {
    NO_LOTTERY_ATTEMPTS("lottery.no_attempts");

    final String key;

    PropertiesMessage(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
