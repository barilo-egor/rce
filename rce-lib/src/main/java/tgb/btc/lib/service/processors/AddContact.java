package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.processors.support.EditContactsService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.ADD_CONTACT)
public class AddContact extends Processor {

    private EditContactsService editContactsService;

    @Autowired
    public void setEditContactsService(EditContactsService editContactsService) {
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
