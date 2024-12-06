package tgb.btc.rce.service.handler.util.impl.handler;

import org.springframework.stereotype.Service;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.sender.IMenuSender;
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
            case ADMIN -> menuSender.send(chatId, PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.ADMIN_PANEL);
            case OPERATOR -> menuSender.send(chatId, PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.OPERATOR_PANEL);
            case OBSERVER -> menuSender.send(chatId, PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.OBSERVER_PANEL);
        }
    }
}
