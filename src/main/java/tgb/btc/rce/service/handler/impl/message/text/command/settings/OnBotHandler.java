package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IBotSwitch;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

@Service
public class OnBotHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IModifyUserService modifyUserService;

    private final IReadUserService readUserService;

    private final IMessagePropertiesService messagePropertiesService;

    private final IMenuService menuService;

    private final IBotSwitch botSwitch;

    public OnBotHandler(IResponseSender responseSender, IModifyUserService modifyUserService,
                        IReadUserService readUserService, IMessagePropertiesService messagePropertiesService,
                        IMenuService menuService, IBotSwitch botSwitch) {
        this.responseSender = responseSender;
        this.modifyUserService = modifyUserService;
        this.readUserService = readUserService;
        this.messagePropertiesService = messagePropertiesService;
        this.menuService = menuService;
        this.botSwitch = botSwitch;
    }

    @Override
    public void handle(Message message) {
        botSwitch.setOn(true);
        Long chatId = message.getChatId();
        responseSender.sendMessage(chatId, "Бот включен.");
        modifyUserService.setDefaultValues(chatId);
        UserRole role = readUserService.getUserRoleByChatId(chatId);
        switch (role) {
            case ADMIN:
                responseSender.sendMessage(chatId,
                        messagePropertiesService.getMessage(PropertiesMessage.MENU_MAIN_ADMIN),
                        menuService.build(Menu.ADMIN_PANEL, role));
                break;
            case OPERATOR:
                responseSender.sendMessage(chatId,
                        messagePropertiesService.getMessage(PropertiesMessage.MENU_MAIN_OPERATOR),
                        menuService.build(Menu.OPERATOR_PANEL, role));
                break;
            case OBSERVER:
                responseSender.sendMessage(chatId, "Вы перешли в панель наблюдателя", Menu.OBSERVER_PANEL);
                break;
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ON_BOT;
    }
}
