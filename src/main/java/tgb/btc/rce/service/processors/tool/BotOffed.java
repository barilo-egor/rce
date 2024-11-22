package tgb.btc.rce.service.processors.tool;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.BOT_OFFED)
public class BotOffed extends Processor {

    private final IMessageImageResponseSender messageImageResponseSender;

    public BotOffed(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Override
    public void run(Update update) {
        messageImageResponseSender.sendMessage(MessageImage.BOT_OFF, UpdateType.getChatId(update));
    }
}
