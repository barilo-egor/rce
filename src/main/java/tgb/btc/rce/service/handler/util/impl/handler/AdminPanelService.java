package tgb.btc.rce.service.handler.util.impl.handler;

import org.springframework.stereotype.Service;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IAdminPanelService;

@Service
public class AdminPanelService implements IAdminPanelService {

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    public AdminPanelService(IReadUserService readUserService, IResponseSender responseSender) {
        this.readUserService = readUserService;
        this.responseSender = responseSender;
    }

    @Override
    public void send(Long chatId) {
        switch (readUserService.getUserRoleByChatId(chatId)) {
            case ADMIN -> responseSender.sendMessage(chatId, PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.ADMIN_PANEL);
            case OPERATOR -> responseSender.sendMessage(chatId, PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.OPERATOR_PANEL);
            case OBSERVER -> responseSender.sendMessage(chatId, PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.OBSERVER_PANEL);
        }
    }
}
