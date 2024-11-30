package tgb.btc.rce.service.handler.impl.message.text.command.settings.contact;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;

@Service
public class DeleteContactHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IContactService contactService;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    public DeleteContactHandler(IResponseSender responseSender, IContactService contactService,
                                ICallbackDataService callbackDataService, IKeyboardBuildService keyboardBuildService) {
        this.responseSender = responseSender;
        this.contactService = contactService;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
    }


    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        List<Contact> contacts = contactService.findAll();
        if (Objects.isNull(contacts) || contacts.isEmpty()) {
            responseSender.sendMessage(chatId, "Список контактов пуст.");
            return;
        }
        List<InlineButton> buttonList = contacts.stream()
                .map(c -> InlineButton.builder()
                        .text(c.getLabel())
                        .data(callbackDataService.buildData(CallbackQueryData.DELETE_CONTACT, c.getPid()))
                        .build()
                )
                .toList();
        responseSender.sendMessage(chatId, "Выберите контакт для удаления", keyboardBuildService.buildInline(buttonList));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.DELETE_CONTACT;
    }
}
