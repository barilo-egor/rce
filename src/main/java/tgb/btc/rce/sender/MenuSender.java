package tgb.btc.rce.sender;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

import java.util.Optional;

@Service
public class MenuSender implements IMenuSender {

    private final IResponseSender responseSender;

    private final IMenuService menuService;

    private final IReadUserService readUserService;

    private final IMessagePropertiesService messagePropertiesService;

    public MenuSender(IResponseSender responseSender, IMenuService menuService, IReadUserService readUserService,
                      IMessagePropertiesService messagePropertiesService) {
        this.responseSender = responseSender;
        this.menuService = menuService;
        this.readUserService = readUserService;
        this.messagePropertiesService = messagePropertiesService;
    }

    @Override
    public Optional<Message> send(Long chatId, PropertiesMessage propertiesMessage, Menu menu) {
        return send(chatId, messagePropertiesService.getMessage(propertiesMessage), menu);
    }

    @Override
    public Optional<Message> send(Long chatId, String text, Menu menu) {
        return responseSender.sendMessage(
                chatId, text,
                menuService.build(menu, readUserService.getUserRoleByChatId(chatId))
        );
    }
}
