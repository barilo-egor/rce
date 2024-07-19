package tgb.btc.rce.enums;


import java.util.Set;

public enum Menu {
    MAIN(2, Set.of(
            Command.BUY_BITCOIN, Command.SELL_BITCOIN, Command.CONTACTS, Command.DRAWS, Command.REFERRAL,
            Command.ADMIN_PANEL, Command.WEB_ADMIN_PANEL, Command.OPERATOR_PANEL
    )),
    DRAWS(2, Set.of(
            Command.LOTTERY, Command.ROULETTE, Command.SLOT_REEL, Command.DICE, Command.RPS, Command.BACK
    )),
    ASK_CONTACT(1, Set.of(Command.SHARE_CONTACT, Command.CANCEL)),
    ADMIN_PANEL(2, Set.of(Command.REQUESTS, Command.BOT_SETTINGS, Command.REPORTS, Command.DISCOUNTS,
            Command.USERS, Command.DEALS_COUNT, Command.QUIT_ADMIN_PANEL)),
    OPERATOR_PANEL(2, Set.of(Command.NEW_DEALS, Command.NEW_API_DEALS, Command.NEW_REVIEWS, Command.NEW_WITHDRAWALS, Command.NEW_SPAM_BANS,
            Command.PAYMENT_TYPES, Command.QUIT_ADMIN_PANEL)),
    DISCOUNTS(2, Set.of(Command.RANK_DISCOUNT, Command.TURN_RANK_DISCOUNT, Command.PERSONAL_BUY_DISCOUNT,
            Command.PERSONAL_SELL_DISCOUNT, Command.REFERRAL_PERCENT, Command.ADMIN_BACK)),
    USERS(2, Set.of(
            Command.SEND_MESSAGES, Command.BAN_UNBAN, Command.USER_INFORMATION, Command.USER_REFERRAL_BALANCE,
            Command.ADMIN_BACK
    )),
    EDIT_CONTACTS(2, Set.of(Command.ADD_CONTACT, Command.DELETE_CONTACT, Command.ADMIN_BACK)),
    ADMIN_BACK(1, Set.of(Command.ADMIN_BACK)),
    SEND_MESSAGES(2, Set.of(Command.MAILING_LIST, Command.SEND_MESSAGE_TO_USER, Command.ADMIN_BACK)),
    BOT_SETTINGS(2, Set.of(Command.CURRENT_DATA, Command.ON_BOT, Command.OFF_BOT, Command.BOT_MESSAGES,
            Command.BOT_VARIABLES, Command.SYSTEM_MESSAGES, Command.PAYMENT_TYPES,
            Command.TURNING_CURRENCY, Command.EDIT_CONTACTS, Command.TURNING_DELIVERY_TYPE,
            Command.ADMIN_BACK)),
    REQUESTS(2, Set.of(
            Command.NEW_DEALS, Command.NEW_API_DEALS,
            Command.NEW_SPAM_BANS, Command.NEW_REVIEWS, Command.NEW_WITHDRAWALS, Command.ADMIN_BACK
    )),
    REPORTS(2, Set.of(Command.CHECKS_FOR_DATE, Command.USERS_REPORT, Command.DEAL_REPORTS, Command.PARTNERS_REPORT,
            Command.USERS_DEALS_REPORT, Command.LOTTERY_REPORT, Command.ADMIN_BACK)),

    PAYMENT_TYPES(2, Set.of(Command.NEW_PAYMENT_TYPE, Command.DELETE_PAYMENT_TYPE, Command.NEW_PAYMENT_TYPE_REQUISITE,
            Command.DELETE_PAYMENT_TYPE_REQUISITE, Command.TURN_PAYMENT_TYPES, Command.CHANGE_MIN_SUM,
            Command.TURN_DYNAMIC_REQUISITES, Command.ADMIN_BACK));

    final int numberOfColumns;

    final Set<Command> commands;

    public Set<Command> getCommands() {
        return commands;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    Menu(int numberOfColumns, Set<Command> commands) {
        this.numberOfColumns = numberOfColumns;
        this.commands = commands;
    }
}
