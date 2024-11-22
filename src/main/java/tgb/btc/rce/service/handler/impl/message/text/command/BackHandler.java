package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class BackHandler implements ITextCommandHandler {

    public final IResponseSender responseSender;

    private final IMessageImageResponseSender messageImageResponseSender;

    public BackHandler(IResponseSender responseSender, IMessageImageResponseSender messageImageResponseSender) {
        this.responseSender = responseSender;
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        messageImageResponseSender.sendMessage(MessageImage.START, chatId);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BACK;
    }
}
