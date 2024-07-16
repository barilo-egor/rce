package tgb.btc.rce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.common.bot.IUserCommonService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

@Slf4j
public abstract class Processor {

    protected IResponseSender responseSender;

    protected IReadUserService readUserService;

    protected IModifyUserService modifyUserService;

    protected IUserCommonService userCommonService;

    protected IMenuService menuService;

    protected IKeyboardBuildService keyboardBuildService;

    protected IKeyboardService keyboardService;

    protected ICallbackQueryService callbackQueryService;
    
    protected IMessagePropertiesService messagePropertiesService;

    protected IUpdateService updateService;

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setCallbackQueryService(ICallbackQueryService callbackQueryService) {
        this.callbackQueryService = callbackQueryService;
    }

    @Autowired
    public void setKeyboardService(IKeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setMenuService(IMenuService menuService) {
        this.menuService = menuService;
    }

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
        modifyUserService.setDefaultValues(updateService.getChatId(update));
    }

    public abstract void run(Update update);

    public boolean checkForCancel(Update update) {
        Long chatId = updateService.getChatId(update);
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
            if (update.hasCallbackQuery() || (update.hasMessage() && update.getMessage().hasText()))
                enteredCommand = Command.fromUpdate(update);
            else return false;
        } catch (BaseException e) {
            return false;
        }
        return command.equals(enteredCommand);
    }

    public void processToMainMenu(Long chatId) {
        modifyUserService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                messagePropertiesService.getMessage(PropertiesMessage.MENU_MAIN),
                menuService.build(Menu.MAIN, readUserService.getUserRoleByChatId(chatId)), "HTML");
    }

    public void processToAdminMainPanel(Long chatId) {
        modifyUserService.setDefaultValues(chatId);
        UserRole role = readUserService.getUserRoleByChatId(chatId);
        if (UserRole.ADMIN.equals(role))
            responseSender.sendMessage(chatId,
                    messagePropertiesService.getMessage(PropertiesMessage.MENU_MAIN_ADMIN),
                    menuService.build(Menu.ADMIN_PANEL, role));
        else if (UserRole.OPERATOR.equals(role))
            responseSender.sendMessage(chatId,
                    messagePropertiesService.getMessage(PropertiesMessage.MENU_MAIN_OPERATOR),
                    menuService.build(Menu.OPERATOR_PANEL, role));
    }

    protected boolean hasMessageText(Update update, String message) {
        if (!updateService.hasMessageText(update)) {
            responseSender.sendMessage(updateService.getChatId(update), message);
            return false;
        }
        return true;
    }
}
