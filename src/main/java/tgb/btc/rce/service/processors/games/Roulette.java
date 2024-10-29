package tgb.btc.rce.service.processors.games;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.ROULETTE)
public class Roulette extends Processor {

    private final IMessageImageResponseSender messageImageResponseSender;

    @Autowired
    public Roulette(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Override
    public void run(Update update) {
        messageImageResponseSender.sendMessage(MessageImage.ROULETTE, updateService.getChatId(update));
    }
}
