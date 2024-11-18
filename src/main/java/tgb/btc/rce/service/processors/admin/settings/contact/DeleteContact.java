package tgb.btc.rce.service.processors.admin.settings.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.EditContactsService;


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
            processToAdminMainPanel(updateService.getChatId(update));
        }
    }
}
