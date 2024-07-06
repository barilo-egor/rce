package tgb.btc.rce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.common.bot.IUserCommonService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@Slf4j
public abstract class Processor {

    protected IResponseSender responseSender;

    protected IReadUserService readUserService;

    protected IModifyUserService modifyUserService;

    protected IUserCommonService userCommonService;

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setUserCommonService(IUserCommonService userCommonService) {
        this.userCommonService = userCommonService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    public void process(Update update) {
        if (checkForCancel(update)) {
            beforeCancel(update);
            return;
        }
        try {
            run(update);
        } catch (Exception e) {
            log.error("Ошибка процессора. ", e);
            throw e;
        }
    }

    public void beforeCancel(Update update) {
        modifyUserService.setDefaultValues(UpdateUtil.getChatId(update));
    }

    public abstract void run(Update update);

    public boolean checkForCancel(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (User.DEFAULT_STEP == readUserService.getStepByChatId(chatId)) return false;
        if (this.getClass().getAnnotation(CommandProcessor.class).command().hasAccess(readUserService.getUserRoleByChatId(chatId)) &&
                (isCommand(update, Command.ADMIN_BACK) || isCommand(update, Command.CANCEL))) {
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
        modifyUserService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.MENU_MAIN),
                getMainMenuKeyboard(chatId), "HTML");
    }

    public void processToAdminMainPanel(Long chatId) {
        modifyUserService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.MENU_MAIN_ADMIN),
                getAdminMainPanel(chatId));
    }

    protected ReplyKeyboard getAdminMainPanel(Long chatId) {
        return MenuFactory.build(Menu.ADMIN_PANEL, readUserService.isAdminByChatId(chatId));
    }

    protected ReplyKeyboard getMainMenuKeyboard(Long chatId) {
        return MenuFactory.build(Menu.MAIN, readUserService.isAdminByChatId(chatId));
    }

    protected boolean hasMessageText(Update update, String message) {
        if (!UpdateUtil.hasMessageText(update)) {
            responseSender.sendMessage(UpdateUtil.getChatId(update), message);
            return false;
        }
        return true;
    }
}
