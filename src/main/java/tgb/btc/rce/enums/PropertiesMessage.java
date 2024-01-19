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
    MENU_MAIN_ADMIN_MESSAGE("menu.main.admin.message"),
    CONTACT_ASK_DELETE("contact.ask.delete"),
    SEND_MESSAGES_MENU("send.messages.menu"),
    EDIT_CONTACTS_MENU("edit.contacts.menu"),
    BOT_SETTINGS_MENU("bot.settings.menu"),
    CHOOSE_CURRENCY_BUY("choose.currency.buy"),

    CHOOSE_CURRENCY_SELL("choose.currency.sell"),
    DEAL_INPUT_SUM("deal.input.sum"),
    DEAL_INPUT_SUM_CRYPTO_OR_FIAT("deal.input.sum.crypto.or.fiat"),
    DEAL_INPUT_SUM_TO_BUY("deal.input.sum.to.buy"),
    DEAL_INPUT_SUM_TO_SELL("deal.input.sum.to.sell"),
    DEAL_CONFIRMED("deal.confirmed"),
    USER_INFORMATION_MAIN("user.information.main"),
    USER_INFORMATION_WITHOUT_REFERRAL_MAIN("user.information.without.referral.main"),

    DELIVERY_TYPE_ASK("delivery.type.ask");

    final String key;

    PropertiesMessage(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
