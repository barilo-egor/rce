package tgb.btc.rce.enums;


import tgb.btc.library.constants.enums.ReferralType;

import java.util.List;

public enum Menu {
    MAIN(ReferralType.STANDARD.isCurrent()
            ? List.of(Command.BUY_BITCOIN, Command.SELL_BITCOIN, Command.CONTACTS, Command.DRAWS, Command.REFERRAL)
            : List.of(Command.BUY_BITCOIN, Command.SELL_BITCOIN, Command.CONTACTS, Command.DRAWS)),
    DRAWS(List.of(Command.LOTTERY, Command.ROULETTE, Command.BACK)),
    ASK_CONTACT(List.of(Command.SHARE_CONTACT, Command.CANCEL)),
    ADMIN_PANEL(List.of(Command.REQUESTS, Command.BOT_SETTINGS, Command.REPORTS, Command.DISCOUNTS,
            Command.USERS, Command.DEALS_COUNT, Command.QUIT_ADMIN_PANEL)),
    DISCOUNTS(ReferralType.STANDARD.isCurrent()
            ? List.of(Command.RANK_DISCOUNT, Command.TURN_RANK_DISCOUNT, Command.PERSONAL_BUY_DISCOUNT,
            Command.PERSONAL_SELL_DISCOUNT, Command.REFERRAL_PERCENT, Command.ADMIN_BACK)
            : List.of(Command.RANK_DISCOUNT, Command.TURN_RANK_DISCOUNT, Command.PERSONAL_BUY_DISCOUNT,
            Command.PERSONAL_SELL_DISCOUNT, Command.ADMIN_BACK)),
    USERS(ReferralType.STANDARD.isCurrent()
            ? List.of(Command.SEND_MESSAGES, Command.BAN_UNBAN, Command.USER_INFORMATION, Command.USER_REFERRAL_BALANCE, Command.ADMIN_BACK)
            : List.of(Command.SEND_MESSAGES, Command.BAN_UNBAN, Command.USER_INFORMATION, Command.ADMIN_BACK)),
    EDIT_CONTACTS(List.of(Command.ADD_CONTACT, Command.DELETE_CONTACT, Command.ADMIN_BACK)),
    ADMIN_BACK(List.of(Command.ADMIN_BACK)),
    SEND_MESSAGES(List.of(Command.MAILING_LIST, Command.SEND_MESSAGE_TO_USER, Command.ADMIN_BACK)),
    BOT_SETTINGS(List.of(Command.CURRENT_DATA, Command.ON_BOT, Command.OFF_BOT, Command.BOT_MESSAGES,
            Command.BOT_VARIABLES, Command.SYSTEM_MESSAGES, Command.PAYMENT_TYPES,
            Command.TURNING_CURRENCY, Command.EDIT_CONTACTS, Command.TURNING_DELIVERY_TYPE,
            Command.ADMIN_BACK)),
    REQUESTS(ReferralType.STANDARD.isCurrent()
            ? List.of(Command.NEW_DEALS, Command.NEW_API_DEALS, Command.NEW_WITHDRAWALS, Command.NEW_REVIEWS, Command.NEW_SPAM_BANS,
            Command.ADMIN_BACK)
            : List.of(Command.NEW_DEALS, Command.NEW_API_DEALS, Command.NEW_REVIEWS, Command.NEW_SPAM_BANS, Command.ADMIN_BACK)),
    REPORTS(List.of(Command.CHECKS_FOR_DATE, Command.USERS_REPORT, Command.DEAL_REPORTS, Command.PARTNERS_REPORT,
            Command.USERS_DEALS_REPORT, Command.LOTTERY_REPORT, Command.ADMIN_BACK)),

    PAYMENT_TYPES(List.of(Command.NEW_PAYMENT_TYPE, Command.DELETE_PAYMENT_TYPE, Command.NEW_PAYMENT_TYPE_REQUISITE,
            Command.DELETE_PAYMENT_TYPE_REQUISITE, Command.TURN_PAYMENT_TYPES, Command.CHANGE_MIN_SUM,
            Command.TURN_DYNAMIC_REQUISITES, Command.ADMIN_BACK));

    final List<Command> commands;

    public List<Command> getCommands() {
        return commands;
    }

    Menu(List<Command> commands) {
        this.commands = commands;
    }
}
