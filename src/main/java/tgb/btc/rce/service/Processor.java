package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
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

    public Processor(IResponseSender responseSender, UserService userService) {
        this.responseSender = responseSender;
        this.userService = userService;
    }

    public abstract void run(Update update);

    public void checkForCancel(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (this.getClass().getAnnotation(CommandProcessor.class).command().isAdmin() &&
                Command.ADMIN_BACK.equals(Command.fromUpdate(update))) processToAdminMainPanel(chatId);
        else if (User.DEFAULT_STEP != userService.getStepByChatId(chatId)
                && UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update))
                && Command.CANCEL.equals(Command.fromUpdate(update)))
            processToMainMenu(chatId);
    }

    public void processToMainMenu(Long chatId) {
        userService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.MENU_MAIN),
                getMainMenuKeyboard(chatId));
    }

    public void processToAdminMainPanel(Long chatId) {
        userService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.MENU_MAIN_ADMIN),
                getAdminMainPanel(chatId));
    }

    protected ReplyKeyboard getAdminMainPanel(Long chatId) {
        return MenuFactory.build(Menu.ADMIN_PANEL, userService.isAdminByChatId(chatId));
    }

    protected ReplyKeyboard getMainMenuKeyboard(Long chatId) {
        return MenuFactory.build(Menu.MAIN, userService.isAdminByChatId(chatId));
    }
}
