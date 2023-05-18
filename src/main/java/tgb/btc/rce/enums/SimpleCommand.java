package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.BeanHolder;
import tgb.btc.rce.util.UpdateUtil;

import java.util.function.Consumer;

public enum SimpleCommand {
    BOT_OFFED(Command.BOT_OFFED, (update) -> BeanHolder.RESPONSE_SENDER.sendBotMessage(
            BeanHolder.BOT_MESSAGE_SERVICE.findByType(BotMessageType.BOT_OFF), UpdateUtil.getChatId(update)
    ));

    final Command command;

    final Consumer<Update> consumer;

    SimpleCommand(Command command, Consumer<Update> consumer) {
        this.command = command;
        this.consumer = consumer;
    }

    public Command getCommand() {
        return command;
    }

    public Consumer<Update> getConsumer() {
        return consumer;
    }

    public static SimpleCommand getByCommand(Command command) {
        for (SimpleCommand simpleCommand : SimpleCommand.values()) {
            if (simpleCommand.getCommand().equals(command)) return simpleCommand;
        }
        throw new BaseException("Не найдена SimpleCommand для " + command.name());
    }
}
