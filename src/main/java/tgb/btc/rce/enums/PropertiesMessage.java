package tgb.btc.rce.enums;

public enum PropertiesMessage {
    NO_LOTTERY_ATTEMPTS("lottery.no_attempts"),
    REFERRAL_MAIN("referral.main"),
    WITHDRAWAL_MIN_SUM("withdrawal.min.sum"),
    WITHDRAWAL_ASK_SUM("withdrawal.ask.sum"),
    MENU_MAIN("menu.main"),
    ADMIN_NOTIFY_WITHDRAWAL_NEW("admin.notify.withdrawal.new"),
    USER_RESPONSE_WITHDRAWAL_REQUEST_CREATED("user.response.withdrawal.created"),

    INSUFFICIENT_FUNDS("withdrawal.insufficient.funds");

    final String key;

    PropertiesMessage(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
