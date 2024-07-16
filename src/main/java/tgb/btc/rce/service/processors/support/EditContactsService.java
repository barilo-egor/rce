package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tgb.btc.rce.constants.BotStringConstants.CALLBACK_DATA_SPLITTER;

@Service
public class EditContactsService {

    private ResponseSender responseSender;

    private IContactService contactService;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private IMenuService menuService;

    private IKeyboardBuildService keyboardBuildService;

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setMenuService(IMenuService menuService) {
        this.menuService = menuService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setContactService(IContactService contactService) {
        this.contactService = contactService;
    }

    public void askInput(Long chatId) {
        modifyUserService.nextStep(chatId, Command.ADD_CONTACT.name());
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.CONTACT_ASK_INPUT),
                menuService.build(Menu.ADMIN_BACK, readUserService.getUserRoleByChatId(chatId)));
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
                keyboardBuildService.buildInline(buildContactButtons(messageId)));
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
