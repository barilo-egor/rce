package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;

@Service
public class ContactsHandler implements ITextCommandHandler {


    private final IContactService contactService;

    private final IMessageImageResponseSender messageImageResponseSender;

    private final IKeyboardBuildService keyboardBuildService;

    public ContactsHandler(IContactService contactService, IMessageImageResponseSender messageImageResponseSender,
                           IKeyboardBuildService keyboardBuildService) {
        this.contactService = contactService;
        this.messageImageResponseSender = messageImageResponseSender;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(Message message) {
        messageImageResponseSender.sendMessage(MessageImage.CONTACTS, message.getChatId(),
                keyboardBuildService.buildContacts(contactService.findAll()));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.CONTACTS;
    }
}
