package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class RouletteHandler implements ITextCommandHandler {

    private final IMessageImageResponseSender messageImageResponseSender;

    public RouletteHandler(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Override
    public void handle(Message message) {
        messageImageResponseSender.sendMessage(MessageImage.ROULETTE, message.getChatId());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ROULETTE;
    }
}
