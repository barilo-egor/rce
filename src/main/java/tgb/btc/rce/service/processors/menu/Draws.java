package tgb.btc.rce.service.processors.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.DRAWS)
public class Draws extends Processor {

    private final IMessageImageResponseSender messageImageResponseSender;

    @Autowired
    public Draws(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Override
    public void run(Update update) {
        messageImageResponseSender.sendMessage(MessageImage.DRAWS, Menu.DRAWS, updateService.getChatId(update));
    }
}
