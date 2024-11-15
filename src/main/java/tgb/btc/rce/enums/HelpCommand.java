package tgb.btc.rce.enums;

import org.apache.commons.lang3.StringUtils;
import tgb.btc.library.exception.BaseException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum HelpCommand {
    MAKE_ADMIN(Command.MAKE_ADMIN, " chatId - перевод пользователя в роль администратора"),
    MAKE_OPERATOR(Command.MAKE_OPERATOR, " chatId - перевод пользователя в роль оператора"),
    MAKE_OBSERVER(Command.MAKE_OBSERVER, " chatId - перевод пользователя в роль наблюдателя"),
    MAKE_USER(Command.MAKE_USER, " chatId - перевод пользователя в роль пользователя"),
    DELETE_USER(Command.DELETE_USER, " chatId - удаление пользователя со всеми его данными(включая сделки)"),
    BACKUP_DB(Command.BACKUP_DB, " создание бэк апа базы данных"),
    HELP(Command.HELP, " - список скрытых команд"),
    DELETE_FROM_POOL(Command.DELETE_FROM_POOL, " id - удаление сделки из пула по её номеру"),
    TURN_NOTIFICATIONS(Command.TURN_NOTIFICATIONS, " - включение/выключение всех оповещений")
    ;

    private final Command command;

    private final String description;

    HelpCommand(Command command, String description) {
        this.command = command;
        this.description = description;
    }

    public static String getDescription(Command command) {
        if (Objects.isNull(command))
            throw new BaseException("Command не может быть null.");
        Optional<HelpCommand> result = Arrays.stream(HelpCommand.values())
                .filter(helpCommand -> helpCommand.command.equals(command))
                .findFirst();
        if (result.isEmpty()) return StringUtils.EMPTY;
        return result.get().description;
    }
}
