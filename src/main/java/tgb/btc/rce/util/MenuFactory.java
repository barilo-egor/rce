package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.DiceType;
import tgb.btc.library.constants.enums.RPS;
import tgb.btc.library.constants.enums.SlotReelType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MenuFactory {
    private MenuFactory() {
    }

    // TODO
    public static ReplyKeyboard build(Menu menu, UserRole userRole) {
        List<ReplyButton> replyButtons;
        switch (menu) {
            case BOT_SETTINGS:
                return botSettings();
            case MAIN:
                replyButtons = main(userRole);
                break;
            case DRAWS:
                replyButtons = draws(userRole);
                break;
            default:
                replyButtons = fillReply(menu.getCommands());
                break;
        }
        return KeyboardUtil.buildReply(menu.getNumberOfColumns(), replyButtons, false);
    }

    private static ReplyKeyboard botSettings() {
        List<Command> commands = new ArrayList<>(Menu.BOT_SETTINGS.getCommands());
        commands.removeIf(command -> (UpdateDispatcher.isOn() && Command.ON_BOT.equals(command)
                || (!UpdateDispatcher.isOn() && Command.OFF_BOT.equals(command))));
        return KeyboardUtil.buildReply(2, fillReply(commands), true);
    }

    private static List<ReplyButton> main(UserRole userRole) {
        List<Command> commands = new ArrayList<>(Menu.MAIN.getCommands());
        if (UserRole.ADMIN.equals(userRole)) {
            commands.add(Command.ADMIN_PANEL);
            commands.add(Command.WEB_ADMIN_PANEL);
        }
        if (UserRole.OPERATOR.equals(userRole)) {
            commands.add(Command.OPERATOR_PANEL);
        }
        return fillReply(commands);
    }

    private static List<ReplyButton> draws(UserRole userRole) {
        List<Command> commands = new ArrayList<>(Menu.DRAWS.getCommands());
        if (SlotReelType.NONE.isCurrent() || (SlotReelType.STANDARD_ADMIN.isCurrent() && UserRole.USER.equals(userRole))) {
            commands.remove(Command.SLOT_REEL);
        }
        if (DiceType.NONE.isCurrent() || (DiceType.STANDARD_ADMIN.isCurrent() && UserRole.USER.equals(userRole))) {
            commands.remove(Command.DICE);
        }
        if (RPS.NONE.isCurrent() || (RPS.STANDARD_ADMIN.isCurrent() && UserRole.USER.equals(userRole))) {
            commands.remove(Command.RPS);
        }
        return fillReply(commands);
    }

    private static List<ReplyButton> fillReply(List<Command> commands) {
        return commands.stream()
                .map(command -> ReplyButton.builder().text(command.getText()).build())
                .collect(Collectors.toList());
    }

}
