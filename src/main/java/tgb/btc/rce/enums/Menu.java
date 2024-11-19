package tgb.btc.rce.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.rce.enums.update.TextCommand;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Menu {
    MAIN(2, List.of(
            Command.BUY_BITCOIN, Command.SELL_BITCOIN, Command.CONTACTS, Command.DRAWS, Command.REFERRAL,
            Command.ADMIN_PANEL, Command.WEB_ADMIN_PANEL, Command.OPERATOR_PANEL, Command.OBSERVER_PANEL
    ),
            List.of(TextCommand.BUY_BITCOIN, TextCommand.SELL_BITCOIN, TextCommand.CONTACTS, TextCommand.DRAWS, TextCommand.REFERRAL,
            TextCommand.ADMIN_PANEL, TextCommand.WEB_ADMIN_PANEL, TextCommand.OPERATOR_PANEL, TextCommand.OBSERVER_PANEL)
    ),
    DRAWS(2, List.of(
            Command.LOTTERY, Command.ROULETTE, Command.SLOT_REEL, Command.DICE, Command.RPS, Command.BACK
    ),
            List.of(TextCommand.LOTTERY, TextCommand.ROULETTE, TextCommand.BACK)
    ),
    ASK_CONTACT(1, List.of(Command.SHARE_CONTACT, Command.CANCEL), List.of()),
    ADMIN_PANEL(2, List.of(Command.REQUESTS, Command.BOT_SETTINGS, Command.REPORTS, Command.DISCOUNTS,
            Command.USERS, Command.DEALS_COUNT, Command.QUIT_ADMIN_PANEL), List.of()),
    OPERATOR_PANEL(2, List.of(Command.NEW_DEALS, Command.NEW_API_DEALS, Command.NEW_REVIEWS, Command.NEW_WITHDRAWALS, Command.NEW_SPAM_BANS,
            Command.PAYMENT_TYPES, Command.QUIT_ADMIN_PANEL), List.of()),
    OBSERVER_PANEL(2, List.of(Command.NEW_DEALS, Command.DEAL_REPORTS, Command.MAILING_LIST, Command.QUIT_ADMIN_PANEL), List.of()),
    DISCOUNTS(2, List.of(Command.RANK_DISCOUNT, Command.TURN_RANK_DISCOUNT, Command.PERSONAL_BUY_DISCOUNT,
            Command.PERSONAL_SELL_DISCOUNT, Command.REFERRAL_PERCENT, Command.ADMIN_BACK), List.of()),
    USERS(2, List.of(
            Command.SEND_MESSAGES, Command.BAN_UNBAN, Command.USER_INFORMATION, Command.USER_REFERRAL_BALANCE,
            Command.ADMIN_BACK
    ), List.of()),
    EDIT_CONTACTS(2, List.of(Command.ADD_CONTACT, Command.DELETE_CONTACT, Command.ADMIN_BACK), List.of()),
    ADMIN_BACK(1, List.of(Command.ADMIN_BACK), List.of()),
    SEND_MESSAGES(2, List.of(Command.MAILING_LIST, Command.SEND_MESSAGE_TO_USER, Command.ADMIN_BACK), List.of()),
    BOT_SETTINGS(2, List.of(Command.CURRENT_DATA, Command.ON_BOT, Command.OFF_BOT, Command.BOT_MESSAGES,
            Command.BOT_VARIABLES, Command.SYSTEM_MESSAGES, Command.PAYMENT_TYPES,
            Command.TURNING_CURRENCY, Command.EDIT_CONTACTS, Command.TURNING_DELIVERY_TYPE,
            Command.ADMIN_BACK), List.of()),
    REQUESTS(2, List.of(
            Command.NEW_DEALS, Command.NEW_API_DEALS, Command.BITCOIN_POOL,
            Command.NEW_SPAM_BANS, Command.NEW_REVIEWS, Command.NEW_WITHDRAWALS, Command.ADMIN_BACK
    ), List.of()),
    REPORTS(2, List.of(Command.CHECKS_FOR_DATE, Command.USERS_REPORT, Command.DEAL_REPORTS, Command.PARTNERS_REPORT,
            Command.USERS_DEALS_REPORT, Command.LOTTERY_REPORT, Command.ADMIN_BACK), List.of()),

    PAYMENT_TYPES(2, List.of(Command.NEW_PAYMENT_TYPE, Command.DELETE_PAYMENT_TYPE, Command.NEW_PAYMENT_TYPE_REQUISITE,
            Command.DELETE_PAYMENT_TYPE_REQUISITE, Command.TURN_PAYMENT_TYPES, Command.CHANGE_MIN_SUM,
            Command.TURN_DYNAMIC_REQUISITES, Command.ADMIN_BACK), List.of());

    final int numberOfColumns;
    final List<Command> commands;
    final List<TextCommand> textCommands;
}
