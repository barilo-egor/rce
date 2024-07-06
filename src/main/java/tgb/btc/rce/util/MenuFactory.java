package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.DiceType;
import tgb.btc.library.constants.enums.RPS;
import tgb.btc.library.constants.enums.SlotReelType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MenuFactory {
    private MenuFactory() {
    }

    // TODO
    public static ReplyKeyboard build(Menu menu, UserRole userRole) {
        switch (menu) {
            case MAIN:
                return KeyboardUtil.buildReply(2, main(userRole), false);
            case DRAWS:
                return KeyboardUtil.buildReply(2, draws(userRole), false);
            case ADMIN_PANEL:
                return KeyboardUtil.buildReply(2, fillReply(Menu.ADMIN_PANEL.getCommands()), false);
            case OPERATOR_PANEL:
                return KeyboardUtil.buildReply(2, fillReply(Menu.OPERATOR_PANEL.getCommands()), false);
            case ASK_CONTACT:
                return KeyboardUtil.buildReply(1, fillReply(Menu.ASK_CONTACT.getCommands()), false);
            case ADMIN_BACK:
                return KeyboardUtil.buildReply(1, fillReply(Menu.ADMIN_BACK.getCommands()), false);
            case SEND_MESSAGES:
                return KeyboardUtil.buildReply(2, fillReply(Menu.SEND_MESSAGES.getCommands()), false);
            case EDIT_CONTACTS:
                return KeyboardUtil.buildReply(2, fillReply(Menu.EDIT_CONTACTS.getCommands()), false);
            case BOT_SETTINGS:
                return botSettings();
            case REQUESTS:
                return KeyboardUtil.buildReply(2, fillReply(Menu.REQUESTS.getCommands()), false);
            case REPORTS:
                return KeyboardUtil.buildReply(2, fillReply(Menu.REPORTS.getCommands()), false);
            case DISCOUNTS:
                return KeyboardUtil.buildReply(2, fillReply(Menu.DISCOUNTS.getCommands()), false);
            case USERS:
                return KeyboardUtil.buildReply(2, fillReply(Menu.USERS.getCommands()), false);
            case PAYMENT_TYPES:
                return KeyboardUtil.buildReply(2, fillReply(Menu.PAYMENT_TYPES.getCommands()), false);
        }
        throw new BaseException("Тип меню " + menu.name() + " не найден.");
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

    public static ReplyKeyboard getLink(String text, String data) {
        return KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text(text)
                        .data(data)
                        .inlineType(InlineType.URL)
                        .build()));
    }

}
