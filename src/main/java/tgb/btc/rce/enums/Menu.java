package tgb.btc.rce.enums;

import java.util.List;

public enum Menu {
    MAIN(List.of(Command.BUY_BITCOIN, Command.SELL_BITCOIN, Command.CONTACTS, Command.LOTTERY, Command.RULES));

    final List<Command> commands;

    public List<Command> getCommands() {
        return commands;
    }

    Menu(List<Command> commands) {
        this.commands = commands;
    }
}
