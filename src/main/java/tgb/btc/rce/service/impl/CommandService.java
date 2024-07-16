package tgb.btc.rce.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.ICommandService;
import tgb.btc.rce.util.UpdateUtil;

@Service
public class CommandService implements ICommandService {

    @Override
    public boolean isStartCommand(Update update) {
        return UpdateUtil.hasMessageText(update) && Command.START.equals(Command.fromUpdate(update));
    }

    @Override
    public boolean isSubmitCommand(Update update) {
        return update.hasCallbackQuery() &&
                (update.getCallbackQuery().getData().startsWith(Command.SUBMIT_LOGIN.name())
                        || update.getCallbackQuery().getData().startsWith(Command.SUBMIT_REGISTER.name()));
    }
}
