package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.service.BeanHolder;
import tgb.btc.rce.util.UpdateUtil;

import java.util.function.Consumer;

public enum SimpleCommand {
    BOT_OFFED(Command.BOT_OFFED, (update) -> BeanHolder.RESPONSE_SENDER.sendBotMessage(
            BeanHolder.BOT_MESSAGE_SERVICE.findByType(BotMessageType.BOT_OFF), UpdateUtil.getChatId(update)
    ));

    final Command command;

    final Consumer<Update> processor;

    SimpleCommand(Command command, Consumer<Update> processor) {
        this.command = command;
        this.processor = processor;
    }

    public Consumer<Update> getProcessor() {
        return processor;
    }
}
