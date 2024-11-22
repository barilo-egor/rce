package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.IStartService;

@Service
public class BackHandler implements ITextCommandHandler {

    public final IResponseSender responseSender;

    private final IStartService startService;

    public BackHandler(IResponseSender responseSender, IStartService startService) {
        this.responseSender = responseSender;
        this.startService = startService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        startService.process(chatId);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BACK;
    }
}
