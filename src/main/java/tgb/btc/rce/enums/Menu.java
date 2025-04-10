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
                    TextCommand.BUY_BITCOIN, TextCommand.SELL_BITCOIN, TextCommand.CONTACTS, TextCommand.DRAWS, TextCommand.REFERRAL,
                    TextCommand.ADMIN_PANEL, TextCommand.WEB_ADMIN_PANEL, TextCommand.OPERATOR_PANEL, TextCommand.OBSERVER_PANEL,
                    TextCommand.CHAT_ADMIN_PANEL
            )
    ),
    DRAWS(2,
            List.of(
                    TextCommand.LOTTERY, TextCommand.ROULETTE, TextCommand.BACK
            )
    ),
    ADMIN_PANEL(2,
            List.of(
                    TextCommand.REQUESTS, TextCommand.BOT_SETTINGS, TextCommand.REPORTS, TextCommand.DISCOUNTS,
                    TextCommand.USERS, TextCommand.DEALS_COUNT, TextCommand.QUIT_ADMIN_PANEL
            )
    ),
    OPERATOR_PANEL(2,
            List.of(
                    TextCommand.REQUESTS, TextCommand.PAYMENT_TYPES, TextCommand.SEND_MESSAGES,
                    TextCommand.USER_REFERRAL_BALANCE, TextCommand.BOT_VARIABLES, TextCommand.QUIT_ADMIN_PANEL
            )
    ),
    OBSERVER_PANEL(2,
            List.of(
                    TextCommand.NEW_DEALS, TextCommand.DEAL_REPORTS, TextCommand.MAILING_LIST, TextCommand.QUIT_ADMIN_PANEL
            )
    ),
    CHAT_ADMIN_PANEL(2,
            List.of(
                    TextCommand.USER_REFERRAL_BALANCE, TextCommand.MAILING_LIST, TextCommand.QUIT_ADMIN_PANEL
            )
    ),
    DISCOUNTS(2,
            List.of(
                    TextCommand.RANK_DISCOUNT, TextCommand.TURN_RANK_DISCOUNT, TextCommand.PERSONAL_BUY_DISCOUNT,
                    TextCommand.PERSONAL_SELL_DISCOUNT, TextCommand.REFERRAL_PERCENT, TextCommand.RETURN
            )
    ),
    USERS(2,
            List.of(
                    TextCommand.SEND_MESSAGES, TextCommand.BAN_UNBAN, TextCommand.USER_INFORMATION, TextCommand.USER_REFERRAL_BALANCE,
                    TextCommand.RETURN
            )
    ),
    EDIT_CONTACTS(2,
            List.of(
                    TextCommand.ADD_CONTACT, TextCommand.DELETE_CONTACT, TextCommand.RETURN
            )
    ),
    ADMIN_BACK(1,
            List.of(
                    TextCommand.RETURN
            )
    ),
    SEND_MESSAGES(2,
            List.of(
                    TextCommand.MAILING_LIST, TextCommand.SEND_MESSAGE_TO_USER, TextCommand.RETURN
            )
    ),
    BOT_SETTINGS(2,
            List.of(
                    TextCommand.ON_BOT, TextCommand.OFF_BOT,
                    TextCommand.BOT_VARIABLES, TextCommand.PAYMENT_TYPES,
                    TextCommand.TURNING_CURRENCY, TextCommand.EDIT_CONTACTS, TextCommand.TURNING_DELIVERY_TYPE,
                    TextCommand.RETURN
            )
    ),
    REQUESTS(2,
            List.of(
                    TextCommand.NEW_DEALS, TextCommand.NEW_API_DEALS, TextCommand.BITCOIN_POOL,
                    TextCommand.NEW_REVIEWS, TextCommand.NEW_WITHDRAWALS, TextCommand.RETURN
            )
    ),
    REPORTS(2,
            List.of(
                    TextCommand.CHECKS_FOR_DATE, TextCommand.USERS_REPORT, TextCommand.DEAL_REPORTS, TextCommand.PARTNERS_REPORT,
                    TextCommand.USERS_DEALS_REPORT, TextCommand.LOTTERY_REPORT, TextCommand.RETURN
            )
    ),

    PAYMENT_TYPES(2,
            List.of(
                    TextCommand.NEW_PAYMENT_TYPE, TextCommand.DELETE_PAYMENT_TYPE, TextCommand.NEW_PAYMENT_TYPE_REQUISITE,
                    TextCommand.DELETE_PAYMENT_TYPE_REQUISITE, TextCommand.TURN_PAYMENT_TYPES, TextCommand.CHANGE_MIN_SUM,
                    TextCommand.TURN_DYNAMIC_REQUISITES, TextCommand.PAYMENT_TYPES_BINDING, TextCommand.PAYMENT_TYPE_DISCOUNTS,
                    TextCommand.ADDITIONAL_PAYMENT_TYPE_TEXT, TextCommand.RETURN
            )
    ),
    PAYMENT_TYPES_BINDING(2,
            List.of(
                    TextCommand.PAYSCROW_BINDING, TextCommand.DASH_PAY_BINDING, TextCommand.ALFA_TEAM_BINDING,
                    TextCommand.ALFA_TEAM_TJS_BINDING, TextCommand.ALFA_TEAM_VTB_BINDING, TextCommand.ALFA_TEAM_ALFA_BINDING,
                    TextCommand.ALFA_TEAM_SBER_BINDING, TextCommand.PAY_POINTS_BINDING, TextCommand.ONLY_PAYS_BINDING,
                    TextCommand.EVO_PAY_BINDING, TextCommand.NICE_PAY_BINDING, TextCommand.RETURN
            )
    );

    final int numberOfColumns;
    final List<TextCommand> textCommands;
}
