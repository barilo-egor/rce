package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.Contact;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.impl.ContactService;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

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
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.CONTACT_ASK_INPUT),
                MenuFactory.build(Menu.ADMIN_BACK, userService.isAdminByChatId(chatId)));
    }

    public void save(Update update) {
        String[] contactData = update.getMessage().getText().split("\n");
        contactService.save(Contact.builder().label(contactData[0]).url(contactData[1]).build());

        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, "Контакт добавлен.");
    }
}
