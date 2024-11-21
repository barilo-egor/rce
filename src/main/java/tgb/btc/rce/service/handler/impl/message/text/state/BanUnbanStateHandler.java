package tgb.btc.rce.service.handler.impl.message.text.state;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.message.text.IStateTextMessageHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.util.IMenuService;

@Service
@Slf4j
public class BanUnbanStateHandler implements IStateTextMessageHandler {

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final IMenuService menuService;

    private final IAdminPanelService adminPanelService;

    private final BannedUserCache bannedUserCache;

    private final BanningUserService banningUserService;

    private final IRedisUserStateService redisUserStateService;

    public BanUnbanStateHandler(IReadUserService readUserService, IResponseSender responseSender,
                                IMenuService menuService, IAdminPanelService adminPanelService,
                                BannedUserCache bannedUserCache, BanningUserService banningUserService,
                                IRedisUserStateService redisUserStateService) {
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.menuService = menuService;
        this.adminPanelService = adminPanelService;
        this.bannedUserCache = bannedUserCache;
        this.banningUserService = banningUserService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (message.getText().equals(TextCommand.ADMIN_BACK.getText())) {
            redisUserStateService.delete(chatId);
        } else {
            Long inputChatId = Long.parseLong(message.getText());
            if (!readUserService.existsByChatId(inputChatId)) {
                responseSender.sendMessage(chatId, "Пользователь с таким ID не найден.",
                        menuService.build(Menu.ADMIN_BACK, readUserService.getUserRoleByChatId(chatId)));
                return;
            }
            if (BooleanUtils.isNotTrue(bannedUserCache.get(inputChatId))) {
                banningUserService.ban(inputChatId);
                responseSender.sendMessage(chatId,
                        "Пользователь заблокирован.");
                log.debug("Админ {} забанил пользователя {}", chatId, inputChatId);
            } else {
                banningUserService.unban(inputChatId);
                responseSender.sendMessage(chatId,
                        "Пользователь разблокирован.");
                log.debug("Админ {} разбанил пользователя {}", chatId, inputChatId);
            }
            redisUserStateService.delete(chatId);
        }
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.BAN_UNBAN;
    }
}
