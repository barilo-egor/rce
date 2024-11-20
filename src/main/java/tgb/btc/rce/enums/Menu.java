package tgb.btc.rce.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.rce.enums.update.TextCommand;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Menu {
    MAIN(2,
            List.of(
                    Command.BUY_BITCOIN, Command.SELL_BITCOIN, Command.CONTACTS, Command.DRAWS, Command.REFERRAL,
                    Command.ADMIN_PANEL, Command.WEB_ADMIN_PANEL, Command.OPERATOR_PANEL, Command.OBSERVER_PANEL
            ),
            List.of(
                    TextCommand.BUY_BITCOIN, TextCommand.SELL_BITCOIN, TextCommand.CONTACTS, TextCommand.DRAWS, TextCommand.REFERRAL,
                    TextCommand.ADMIN_PANEL, TextCommand.WEB_ADMIN_PANEL, TextCommand.OPERATOR_PANEL, TextCommand.OBSERVER_PANEL
            )
    ),
    DRAWS(2,
            List.of(
                    Command.LOTTERY, Command.ROULETTE, Command.BACK
            ),
            List.of(
                    TextCommand.LOTTERY, TextCommand.ROULETTE, TextCommand.BACK
            )
    ),
    ADMIN_PANEL(2,
            List.of(
                    Command.REQUESTS, Command.BOT_SETTINGS, Command.REPORTS, Command.DISCOUNTS,
                    Command.USERS, Command.DEALS_COUNT, Command.QUIT_ADMIN_PANEL
            ),
            List.of(
                    TextCommand.REQUESTS, TextCommand.BOT_SETTINGS, TextCommand.REPORTS, TextCommand.DISCOUNTS,
                    TextCommand.USERS, TextCommand.DEALS_COUNT, TextCommand.QUIT_ADMIN_PANEL
            )
    ),
    OPERATOR_PANEL(2,
            List.of(
                    Command.NEW_DEALS, Command.NEW_API_DEALS, Command.NEW_REVIEWS, Command.NEW_WITHDRAWALS, Command.NEW_SPAM_BANS,
                    Command.PAYMENT_TYPES, Command.QUIT_ADMIN_PANEL
            ),
            List.of(
                    TextCommand.NEW_DEALS, TextCommand.NEW_API_DEALS, TextCommand.NEW_REVIEWS, TextCommand.NEW_WITHDRAWALS, TextCommand.NEW_SPAM_BANS,
                    TextCommand.PAYMENT_TYPES, TextCommand.QUIT_ADMIN_PANEL
            )
    ),
    OBSERVER_PANEL(2,
            List.of(
                    Command.NEW_DEALS, Command.DEAL_REPORTS, Command.MAILING_LIST, Command.QUIT_ADMIN_PANEL
            ),
            List.of(
                    TextCommand.NEW_DEALS, TextCommand.DEAL_REPORTS, TextCommand.MAILING_LIST, TextCommand.QUIT_ADMIN_PANEL
            )
    ),
    DISCOUNTS(2,
            List.of(
                    Command.RANK_DISCOUNT, Command.TURN_RANK_DISCOUNT, Command.PERSONAL_BUY_DISCOUNT,
                    Command.PERSONAL_SELL_DISCOUNT, Command.REFERRAL_PERCENT, Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.RANK_DISCOUNT, TextCommand.TURN_RANK_DISCOUNT, TextCommand.PERSONAL_BUY_DISCOUNT,
                    TextCommand.PERSONAL_SELL_DISCOUNT, TextCommand.REFERRAL_PERCENT, TextCommand.ADMIN_BACK
            )
    ),
    USERS(2,
            List.of(
                    Command.SEND_MESSAGES, Command.BAN_UNBAN, Command.USER_INFORMATION, Command.USER_REFERRAL_BALANCE,
                    Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.SEND_MESSAGES, TextCommand.BAN_UNBAN, TextCommand.USER_INFORMATION, TextCommand.USER_REFERRAL_BALANCE,
                    TextCommand.ADMIN_BACK
            )
    ),
    EDIT_CONTACTS(2,
            List.of(
                    Command.ADD_CONTACT, Command.DELETE_CONTACT, Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.ADD_CONTACT, TextCommand.DELETE_CONTACT, TextCommand.ADMIN_BACK
            )
    ),
    ADMIN_BACK(1,
            List.of(
                    Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.ADMIN_BACK
            )
    ),
    SEND_MESSAGES(2,
            List.of(
                    Command.MAILING_LIST, Command.SEND_MESSAGE_TO_USER, Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.MAILING_LIST, TextCommand.SEND_MESSAGE_TO_USER, TextCommand.ADMIN_BACK
            )
    ),
    BOT_SETTINGS(2,
            List.of(
                    Command.CURRENT_DATA, Command.ON_BOT, Command.OFF_BOT, Command.BOT_MESSAGES,
                    Command.BOT_VARIABLES, Command.SYSTEM_MESSAGES, Command.PAYMENT_TYPES,
                    Command.TURNING_CURRENCY, Command.EDIT_CONTACTS, Command.TURNING_DELIVERY_TYPE,
                    Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.CURRENT_DATA, TextCommand.ON_BOT, TextCommand.OFF_BOT, TextCommand.BOT_MESSAGES,
                    TextCommand.BOT_VARIABLES, TextCommand.SYSTEM_MESSAGES, TextCommand.PAYMENT_TYPES,
                    TextCommand.TURNING_CURRENCY, TextCommand.EDIT_CONTACTS, TextCommand.TURNING_DELIVERY_TYPE,
                    TextCommand.ADMIN_BACK
            )
    ),
    REQUESTS(2,
            List.of(
                    Command.NEW_DEALS, Command.NEW_API_DEALS, Command.BITCOIN_POOL,
                    Command.NEW_SPAM_BANS, Command.NEW_REVIEWS, Command.NEW_WITHDRAWALS, Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.NEW_DEALS, TextCommand.NEW_API_DEALS, TextCommand.BITCOIN_POOL,
                    TextCommand.NEW_SPAM_BANS, TextCommand.NEW_REVIEWS, TextCommand.NEW_WITHDRAWALS, TextCommand.ADMIN_BACK
            )
    ),
    REPORTS(2,
            List.of(
                    Command.CHECKS_FOR_DATE, Command.USERS_REPORT, Command.DEAL_REPORTS, Command.PARTNERS_REPORT,
                    Command.USERS_DEALS_REPORT, Command.LOTTERY_REPORT, Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.CHECKS_FOR_DATE, TextCommand.USERS_REPORT, TextCommand.DEAL_REPORTS, TextCommand.PARTNERS_REPORT,
                    TextCommand.USERS_DEALS_REPORT, TextCommand.LOTTERY_REPORT, TextCommand.ADMIN_BACK
            )
    ),

    PAYMENT_TYPES(2,
            List.of(
                    Command.NEW_PAYMENT_TYPE, Command.DELETE_PAYMENT_TYPE, Command.NEW_PAYMENT_TYPE_REQUISITE,
                    Command.DELETE_PAYMENT_TYPE_REQUISITE, Command.TURN_PAYMENT_TYPES, Command.CHANGE_MIN_SUM,
                    Command.TURN_DYNAMIC_REQUISITES, Command.ADMIN_BACK
            ),
            List.of(
                    TextCommand.NEW_PAYMENT_TYPE, TextCommand.DELETE_PAYMENT_TYPE, TextCommand.NEW_PAYMENT_TYPE_REQUISITE,
                    TextCommand.DELETE_PAYMENT_TYPE_REQUISITE, TextCommand.TURN_PAYMENT_TYPES, TextCommand.CHANGE_MIN_SUM,
                    TextCommand.TURN_DYNAMIC_REQUISITES, TextCommand.ADMIN_BACK
            )
    );

    final int numberOfColumns;
    final List<Command> commands;
    final List<TextCommand> textCommands;
}
