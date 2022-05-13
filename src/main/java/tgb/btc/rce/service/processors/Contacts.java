package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.ContactService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.stream.Collectors;

@CommandProcessor(command = Command.CONTACTS)
public class Contacts extends Processor {
    private final BotMessageService botMessageService;
    private final ContactService contactService;

    @Autowired
    public Contacts(IResponseSender responseSender, UserService userService, BotMessageService botMessageService,
                    ContactService contactService) {
        super(responseSender, userService);
        this.botMessageService = botMessageService;
        this.contactService = contactService;
    }

    @Override
    public void run(Update update) {
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.CONTACTS),
                UpdateUtil.getChatId(update),
                getContactsKeyboard());
    }

    private ReplyKeyboard getContactsKeyboard() {
        return KeyboardUtil.buildInline(
                contactService.findAll().stream()
                        .map(contact -> InlineButton.builder()
                                .text(contact.getLabel())
                                .data(contact.getUrl())
                                .build())
                        .collect(Collectors.toList()),
                InlineType.URL);
    }
}
