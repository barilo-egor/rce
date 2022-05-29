package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.EditContactsService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.ADD_CONTACT)
public class AddContact extends Processor {

    private final EditContactsService editContactsService;

    @Autowired
    public AddContact(IResponseSender responseSender, UserService userService, EditContactsService editContactsService) {
        super(responseSender, userService);
        this.editContactsService = editContactsService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                editContactsService.askInput(chatId);
                break;
            case 1:
                editContactsService.save(update);
                processToAdminMainPanel(UpdateUtil.getChatId(update));
                break;
        }
    }
}
