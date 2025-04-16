package tgb.btc.rce.service.handler.util.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IBotSwitch;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.util.IUpdateFilterService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.IMenuService;

import java.util.Objects;

@Service
public class UpdateFilterService implements IUpdateFilterService {

    private final IBotSwitch botSwitch;

    private final IReadUserService readUserService;

    private final IUpdateService updateService;

    private final IMenuService menuService;

    private final IRedisUserStateService redisUserStateService;

    public UpdateFilterService(IBotSwitch botSwitch, IReadUserService readUserService, IUpdateService updateService,
                               IMenuService menuService, IRedisUserStateService redisUserStateService) {
        this.botSwitch = botSwitch;
        this.readUserService = readUserService;
        this.updateService = updateService;
        this.menuService = menuService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public UpdateFilterType getType(Update update) {
        if (updateService.isGroupMessage(update)) {
            return UpdateFilterType.GROUP;
        }
        Long chatId = UpdateType.getChatId(update);
        UserRole userRole = readUserService.getUserRoleByChatId(chatId);
        if (UserRole.USER.equals(userRole) && !botSwitch.isOn()) return UpdateFilterType.BOT_OFFED;
        if (updateService.hasMessageText(update)) {
            if (update.getMessage().getText().startsWith(SlashCommand.START.getText())) {
                return UpdateFilterType.START;
            } else if (Objects.nonNull(redisUserStateService.get(chatId))
                    && menuService.isMenuCommand(Menu.MAIN, update.getMessage().getText())) {
                return UpdateFilterType.MAIN_MENU;
            }
        }
        return null;
    }
}
