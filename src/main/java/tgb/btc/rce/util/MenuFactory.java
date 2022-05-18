package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MenuFactory {
    private MenuFactory() {
    }

// TODO Егор
//  Этот метод сейчас принимает булеан примитив. Сейчас эта переменная используется только в одном кейсе,
//  а во всех местах, где вызывается этот метод, делается запрос в базу чтобы уточнить, является ли админом юзер.
//  Нужно изменить этот параметр на обертку, чтобы была возможность передать в этот метод null, а во всех местах,
//  где вызывается этот метод, и где эта переменная не будет нужна в этом методе, заменить вызов метода по уточнению
//  является ли юзер админом на null.
//
    public static ReplyKeyboard build(Menu menu, boolean isAdmin) {
        switch (menu) {
            case MAIN:
                return KeyboardUtil.buildReply(2, main(isAdmin), false);
            case DRAWS:
                return KeyboardUtil.buildReply(2, fillReply(Menu.DRAWS.getCommands()), false);
            case ADMIN_PANEL:
                return KeyboardUtil.buildReply(2, fillReply(Menu.ADMIN_PANEL.getCommands()), false);
            case ASK_CONTACT:
                return KeyboardUtil.buildReply(1, fillReply(Menu.ASK_CONTACT.getCommands()), false);
            case ADMIN_BACK:
                return KeyboardUtil.buildReply(1, fillReply(Menu.ADMIN_BACK.getCommands()), true);
            case SEND_MESSAGES:
                return KeyboardUtil.buildReply(2, fillReply(Menu.SEND_MESSAGES.getCommands()), true);
            case EDIT_CONTACTS:
                // TODO
        }
        throw new BaseException("Тип меню " + menu.name() + " не найден.");
    }

    private static List<ReplyButton> main(boolean isAdmin) {
        List<Command> commands = new ArrayList<>(Menu.MAIN.getCommands());
        if (isAdmin) commands.add(Command.ADMIN_PANEL);
        return fillReply(commands);
    }

    private static List<ReplyButton> fillReply(List<Command> commands) {
        return commands.stream()
                .map(command -> ReplyButton.builder().text(command.getText()).build())
                .collect(Collectors.toList());
    }

    public static ReplyKeyboard getLink(String text, String data) {
        return KeyboardUtil.buildInline(List.of(InlineButton.builder().text(text)
                .data(data).build()), InlineType.URL);
    }
}
