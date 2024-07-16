package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.util.ICommandService;

@Service
public class CommandService implements ICommandService {

    private IUpdateService updateService;

    @Autowired
    public CommandService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Override
    public boolean isStartCommand(Update update) {
        return updateService.hasMessageText(update) && Command.START.equals(Command.fromUpdate(update));
    }

    @Override
    public boolean isSubmitCommand(Update update) {
        return update.hasCallbackQuery() &&
                (update.getCallbackQuery().getData().startsWith(Command.SUBMIT_LOGIN.name())
                        || update.getCallbackQuery().getData().startsWith(Command.SUBMIT_REGISTER.name()));
    }
}
