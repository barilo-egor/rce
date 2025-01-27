package tgb.btc.rce.service.handler.util.impl.handler;

import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.service.handler.impl.message.text.command.menu.ChatAdminPanelHandler;
import tgb.btc.rce.service.handler.impl.message.text.command.menu.ObserverPanelHandler;
import tgb.btc.rce.service.handler.impl.message.text.command.menu.OperatorPanelHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;

@Service
public class AdminPanelService implements IAdminPanelService {

    private final IReadUserService readUserService;

    private final IMenuSender menuSender;

    public AdminPanelService(IReadUserService readUserService, IMenuSender menuSender) {
        this.readUserService = readUserService;
        this.menuSender = menuSender;
    }

    @Override
    public void send(Long chatId) {
        switch (readUserService.getUserRoleByChatId(chatId)) {
            case ADMIN -> menuSender.send(chatId, "Вы перешли в админ панель.", Menu.ADMIN_PANEL);
            case OPERATOR -> menuSender.send(chatId, OperatorPanelHandler.MESSAGE, Menu.OPERATOR_PANEL);
            case OBSERVER -> menuSender.send(chatId, ObserverPanelHandler.MESSAGE, Menu.OBSERVER_PANEL);
            case CHAT_ADMIN -> menuSender.send(chatId, ChatAdminPanelHandler.MESSAGE, Menu.CHAT_ADMIN_PANEL);
            default -> throw new BaseException("Роль либо отсутствует, либо для неё не предусмотрена реализация админ панели.");
        }
    }
}
