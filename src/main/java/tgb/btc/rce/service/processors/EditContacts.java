package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.EDIT_CONTACTS)
public class EditContacts extends Processor {

    @Autowired
    public EditContacts(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.EDIT_CONTACTS_MENU),
                MenuFactory.build(Menu.EDIT_CONTACTS, userService.isAdminByChatId(chatId)));
    }
}
