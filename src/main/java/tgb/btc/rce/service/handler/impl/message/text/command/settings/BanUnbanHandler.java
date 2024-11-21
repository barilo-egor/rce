package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.processors.support.MessagesService;

@Service
public class BanUnbanHandler implements ITextCommandHandler {

    private final MessagesService messagesService;

    public BanUnbanHandler(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public void handle(Message message) {
        messagesService.askForChatId(message.getChatId(), UserState.BAN_UNBAN);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BAN_UNBAN;
    }
}
