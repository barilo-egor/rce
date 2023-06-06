package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.UpdateType;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.processors.support.EditContactsService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_CONTACT)
public class DeleteContact extends Processor {

    private EditContactsService editContactsService;

    @Autowired
    public void setEditContactsService(EditContactsService editContactsService) {
        this.editContactsService = editContactsService;
    }

    @Override
    public void run(Update update) {
        if (UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update)))
            editContactsService.askForChoose(update);
        else if (UpdateType.CALLBACK_QUERY.equals(UpdateType.fromUpdate(update))) {
            editContactsService.delete(update);
            processToAdminMainPanel(UpdateUtil.getChatId(update));
        }
    }
}
