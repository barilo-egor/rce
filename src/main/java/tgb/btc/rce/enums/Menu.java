package tgb.btc.rce.enums;

import java.util.List;

public enum Menu {
    MAIN(List.of(Command.BUY_BITCOIN, Command.SELL_BITCOIN, Command.CONTACTS, Command.DRAWS, Command.REFERRAL)),
    DRAWS(List.of(Command.LOTTERY, Command.ROULETTE, Command.BACK)),
    ASK_CONTACT(List.of(Command.SHARE_CONTACT, Command.CANCEL)),
    ADMIN_PANEL(List.of(Command.REQUESTS, Command.SEND_MESSAGES, Command.BAN_UNBAN, Command.BOT_SETTINGS,
            Command.REPORTS, Command.EDIT_CONTACTS, Command.QUIT_ADMIN_PANEL)),
    EDIT_CONTACTS(List.of(Command.ADD_CONTACT, Command.DELETE_CONTACT, Command.ADMIN_BACK)),
    ADMIN_BACK(List.of(Command.ADMIN_BACK)),
    SEND_MESSAGES(List.of(Command.MAILING_LIST, Command.SEND_MESSAGE_TO_USER, Command.ADMIN_BACK)),
    BOT_SETTINGS(List.of(Command.CURRENT_DATA, Command.ON_BOT, Command.OFF_BOT, Command.BOT_MESSAGES,
            Command.BOT_VARIABLES, Command.SYSTEM_MESSAGES, Command.ADMIN_BACK));

    final List<Command> commands;

    public List<Command> getCommands() {
        return commands;
    }

    Menu(List<Command> commands) {
        this.commands = commands;
    }
}
