package tgb.btc.rce.service.handler.impl.state.users;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.IUserInfoService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.util.Objects;

@Service
public class UserInformationStateHandler implements IStateHandler {

    private final IRedisUserStateService redisUserStateService;

    private final IUserInfoService userInfoService;

    private final IUserInputService userInputService;

    private final IAdminPanelService adminPanelService;

    public UserInformationStateHandler(IRedisUserStateService redisUserStateService,
                                       IUserInfoService userInfoService, IUserInputService userInputService,
                                       IAdminPanelService adminPanelService) {
        this.redisUserStateService = redisUserStateService;
        this.userInfoService = userInfoService;
        this.userInputService = userInputService;
        this.adminPanelService = adminPanelService;
    }

    @Override
    public void handle(Update update) {
        if (!userInputService.hasTextInput(update)) {
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        Long userChatId = userInputService.getInputChatId(chatId, text);
        if (Objects.isNull(userChatId)) {
            return;
        }
        userInfoService.sendUserInformation(chatId, userChatId);
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.USER_INFORMATION;
    }
}
