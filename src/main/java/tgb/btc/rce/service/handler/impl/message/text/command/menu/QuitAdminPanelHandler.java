package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

@Service
public class QuitAdminPanelHandler implements ITextCommandHandler {

    private final IModifyUserService modifyUserService;

    private final IResponseSender responseSender;

    private final IMessagePropertiesService messagePropertiesService;

    private final IMenuService menuService;

    private final IReadUserService readUserService;

    public QuitAdminPanelHandler(IModifyUserService modifyUserService, IResponseSender responseSender,
                                 IMessagePropertiesService messagePropertiesService, IMenuService menuService,
                                 IReadUserService readUserService) {
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
        this.messagePropertiesService = messagePropertiesService;
        this.menuService = menuService;
        this.readUserService = readUserService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        modifyUserService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                messagePropertiesService.getMessage(PropertiesMessage.MENU_MAIN),
                menuService.build(Menu.MAIN, readUserService.getUserRoleByChatId(chatId)), "HTML");
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.QUIT_ADMIN_PANEL;
    }
}
