package tgb.btc.rce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

public abstract class Processor {
    @Autowired
    protected IResponseSender responseSender;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;

    public void process(Update update) {
        if (checkForCancel(update)) {
            beforeCancel(update);
            return;
        }
        run(update);
    }

    public void beforeCancel(Update update) {
        userService.setDefaultValues(UpdateUtil.getChatId(update));
    }

    public abstract void run(Update update);

    public boolean checkForCancel(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (User.DEFAULT_STEP == userService.getStepByChatId(chatId)) return false;
        if (this.getClass().getAnnotation(CommandProcessor.class).command().isAdmin() &&
                (isCommand(update, Command.ADMIN_BACK)) || isCommand(update, Command.CANCEL)) {
            processToAdminMainPanel(chatId);
            return true;
        } else if (UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update))
                && isCommand(update, Command.CANCEL)) {
            processToMainMenu(chatId);
            return true;
        } else return false;
    }

    private boolean isCommand(Update update, Command command) {
        Command enteredCommand;
        try {
            if(update.hasCallbackQuery() || (update.hasMessage() && update.getMessage().hasText())) enteredCommand = Command.fromUpdate(update);
            else return false;
        } catch (BaseException e) {
            return false;
        }
        return command.equals(enteredCommand);
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

    protected boolean hasMessageText(Update update, String message) {
        if (!UpdateUtil.hasMessageText(update)) {
            responseSender.sendMessage(UpdateUtil.getChatId(update), message);
            return false;
        }
        return true;
    }
}
