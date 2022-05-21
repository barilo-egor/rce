package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.Contact;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.impl.ContactService;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tgb.btc.rce.constants.BotStringConstants.*;

@Service
public class EditContactsService {

    private final ResponseSender responseSender;

    private final ContactService contactService;

    private final UserService userService;

    @Autowired
    public EditContactsService(ResponseSender responseSender, ContactService contactService, UserService userService) {
        this.responseSender = responseSender;
        this.contactService = contactService;
        this.userService = userService;
    }

    public void askInput(Long chatId) {
        userService.nextStep(chatId, Command.ADD_CONTACT);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.CONTACT_ASK_INPUT),
                MenuFactory.build(Menu.ADMIN_BACK, userService.isAdminByChatId(chatId)));
    }

    public void save(Update update) {
        String[] contactData = update.getMessage().getText().split("\n");
        contactService.save(Contact.builder().label(contactData[0]).url(contactData[1]).build());

        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, "Контакт добавлен.");
    }

    public void askForChoose(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Optional<Message> optionalMessage =
                responseSender.sendMessage(chatId, "Выберите контакт для удаления.");
        if (optionalMessage.isEmpty()) throw new BaseException("Не получено отправленное сообщение");
        Integer messageId = optionalMessage.get().getMessageId();
        responseSender.sendEditedMessageText(chatId, messageId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.CONTACT_ASK_DELETE),
                KeyboardUtil.buildInline(buildContactButtons(messageId), InlineType.CALLBACK_DATA));
    }

    private List<InlineButton> buildContactButtons(Integer messageId) {
        return contactService.findAll().stream()
                .map(c -> InlineButton.builder()
                        .text(c.getLabel())
                        .data(Command.DELETE_CONTACT.getText() + CALLBACK_DATA_SPLITTER
                                + c.getPid() + CALLBACK_DATA_SPLITTER
                                + messageId).build())
                .collect(Collectors.toList());
    }

    public void delete(Update update) {
        contactService.deleteById(Long.valueOf(update.getCallbackQuery().getData().split(CALLBACK_DATA_SPLITTER)[1]));
        responseSender.deleteMessage(update.getCallbackQuery().getFrom().getId(),
                Integer.valueOf(update.getCallbackQuery().getData().split(CALLBACK_DATA_SPLITTER)[2]));
        responseSender.sendMessage(update.getCallbackQuery().getFrom().getId(), "Контакт удален.");
    }
}
