package tgb.btc.rce.service.handler.util.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IBotSwitch;
import tgb.btc.rce.service.handler.util.IUpdateFilterService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class UpdateFilterService implements IUpdateFilterService {

    private final IRedisUserStateService redisUserStateService;

    private final IBotSwitch botSwitch;

    private final IReadUserService readUserService;

    public UpdateFilterService(IRedisUserStateService redisUserStateService, IBotSwitch botSwitch,
                               IReadUserService readUserService) {
        this.redisUserStateService = redisUserStateService;
        this.botSwitch = botSwitch;
        this.readUserService = readUserService;
    }

    @Override
    public UpdateFilterType getType(Update update) {
        UserRole userRole = readUserService.getUserRoleByChatId(UpdateType.getChatId(update));
        if (UserRole.USER.equals(userRole) && !botSwitch.isOn()) return UpdateFilterType.BOT_OFFED;
        if (UserRole.USER.equals(userRole) && UserState.CAPTCHA.equals(redisUserStateService.get(UpdateType.getChatId(update))))
            return UpdateFilterType.CAPTCHA;
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(SlashCommand.START.getText()))
            return UpdateFilterType.START;
        return null;
    }
}
