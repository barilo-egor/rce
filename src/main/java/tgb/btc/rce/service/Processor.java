package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

public abstract class Processor {
    protected IResponseSender responseSender;
    protected UserService userService;

    public Processor(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    public abstract void run(Update update);

    public void checkForCancel(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (User.DEFAULT_STEP != userService.getStepByChatId(chatId)
                && UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update))
                && Command.CANCEL.equals(Command.fromUpdate(update))) processToMainMenu(chatId);
    }

    public void processToMainMenu(Long chatId) {
        userService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.MENU_MAIN),
                getMainMenuKeyboard(chatId));
    }

    protected ReplyKeyboard getMainMenuKeyboard(Long chatId) {
        return MenuFactory.build(Menu.MAIN, userService.isAdminByChatId(chatId));
    }
}
