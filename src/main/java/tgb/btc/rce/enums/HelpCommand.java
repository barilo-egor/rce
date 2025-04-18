package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.update.SlashCommand;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum HelpCommand {
    MAKE_ADMIN(SlashCommand.MAKE_ADMIN, " chatId - перевод пользователя в роль администратора"),
    MAKE_OPERATOR(SlashCommand.MAKE_OPERATOR, " chatId - перевод пользователя в роль оператора"),
    MAKE_OBSERVER(SlashCommand.MAKE_OBSERVER, " chatId - перевод пользователя в роль наблюдателя"),
    MAKE_CHAT_ADMIN(SlashCommand.MAKE_CHAT_ADMIN, " chatId - перевод пользователя в роль администратора чата"),
    MAKE_USER(SlashCommand.MAKE_USER, " chatId - перевод пользователя в роль пользователя"),
    DELETE_USER(SlashCommand.DELETE_USER, " chatId - удаление пользователя со всеми его данными(включая сделки)"),
    BACKUP_DB(SlashCommand.BACKUP_DB, " создание бэк апа базы данных"),
    HELP(SlashCommand.HELP, " - список скрытых команд"),
    DELETE_FROM_POOL(SlashCommand.DELETE_FROM_POOL, " id - удаление сделки из пула по её номеру"),
    TURN_NOTIFICATIONS(SlashCommand.NOTIFICATIONS, " - включение/выключение всех оповещений")
    ;

    private final SlashCommand command;

    private final String description;

    public static String getDescription(SlashCommand command) {
        if (Objects.isNull(command))
            throw new BaseException("Command не может быть null.");
        Optional<HelpCommand> result = Arrays.stream(HelpCommand.values())
                .filter(helpCommand -> helpCommand.command.equals(command))
                .findFirst();
        if (result.isEmpty()) return StringUtils.EMPTY;
        return result.get().description;
    }
}
