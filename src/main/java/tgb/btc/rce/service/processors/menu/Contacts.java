package tgb.btc.rce.service.processors.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.CONTACTS)
public class Contacts extends Processor {

    private IContactService contactService;

    private IMessageImageResponseSender messageImageResponseSender;

    @Autowired
    public void setMessageImageResponseSender(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Autowired
    public void setContactService(IContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public void run(Update update) {
//        responseSender.sendBotMessage(BotMessageType.CONTACTS, updateService.getChatId(update),
//                keyboardBuildService.buildContacts(contactService.findAll()));
        messageImageResponseSender.sendMessage(MessageImage.CONTACTS, updateService.getChatId(update),
                keyboardBuildService.buildContacts(contactService.findAll()));
    }
}
