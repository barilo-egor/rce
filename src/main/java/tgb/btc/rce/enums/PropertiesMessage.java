package tgb.btc.rce.enums;

public enum PropertiesMessage {
    NO_LOTTERY_ATTEMPTS("lottery.no_attempts"),
    REFERRAL_MAIN("referral.main"),
    WITHDRAWAL_MIN_SUM("withdrawal.min.sum"),
    WITHDRAWAL_ASK_CONTACT("withdrawal.ask.contact"),
    MENU_MAIN("menu.main"),
    ADMIN_NOTIFY_WITHDRAWAL_NEW("admin.notify.withdrawal.new"),
    USER_RESPONSE_WITHDRAWAL_REQUEST_CREATED("user.response.withdrawal.created"),
    WITHDRAWAL_ERROR_CONTACT("withdrawal.error.contact"),
    WITHDRAWAL_TO_STRING("withdrawal.to.string"),
    CONTACT_ASK_INPUT("contact.ask.input"),
    MENU_MAIN_ADMIN("menu.main.admin"),
    CONTACT_ASK_DELETE("contact.ask.delete"),
    SEND_MESSAGES_MENU("send.messages.menu"),
    EDIT_CONTACTS_MENU("edit.contacts.menu"),
    BOT_SETTINGS_MENU("bot.settings.menu"),
    CHOOSE_CURRENCY("choose.currency"),
    DEAL_INPUT_SUM("deal.input.sum");

    final String key;

    PropertiesMessage(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
