package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.constants.BotNumberConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.util.UpdateUtil;

@Service
public class UpdateDispatcher implements IUpdateDispatcher {

    private UserService userService;

    @Autowired
    public UpdateDispatcher(UserService userService) {
        this.userService = userService;
    }

    public void dispatch(Update update) {
        Command command = getCommand(update);
    }

    private Command getCommand(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        return userService.getStepByChatId(chatId) == BotNumberConstants.DEFAULT_STEP ? Command.fromUpdate(update)
                : userService.getCommandByChatId(chatId);
    }
}
