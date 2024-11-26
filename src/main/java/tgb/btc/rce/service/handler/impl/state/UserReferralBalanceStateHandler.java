package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;

@Service
public class UserReferralBalanceStateHandler implements IStateHandler {

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    private final IAdminPanelService adminPanelService;

    private final IRedisUserStateService redisUserStateService;

    private final ICallbackDataService callbackDataService;

    private final IUserInputService userInputService;

    public UserReferralBalanceStateHandler(IReadUserService readUserService, IResponseSender responseSender,
                                           IKeyboardBuildService keyboardBuildService,
                                           IAdminPanelService adminPanelService,
                                           IRedisUserStateService redisUserStateService,
                                           ICallbackDataService callbackDataService, IUserInputService userInputService) {
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
        this.adminPanelService = adminPanelService;
        this.redisUserStateService = redisUserStateService;
        this.callbackDataService = callbackDataService;
        this.userInputService = userInputService;
    }

    @Override
    public void handle(Update update) {
        if (!userInputService.hasTextInput(update)) {
            return;
        }
        Long chatId = update.getMessage().getChatId();
        Long userChatId = userInputService.getInputChatId(chatId, update.getMessage().getText());
        if (Objects.isNull(userChatId)) {
            return;
        }
        if (readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "У пользователя с чат айди <b>" + userChatId
                            + "</b> на реферальном балансе <b>" + readUserService.getReferralBalanceByChatId(userChatId) + "₽</b>.",
                    keyboardBuildService.buildInline(List.of(
                            InlineButton.builder()
                                    .inlineType(InlineType.CALLBACK_DATA)
                                    .text("Изменить")
                                    .data(callbackDataService.buildData(CallbackQueryData.CHANGE_REFERRAL_BALANCE, userChatId))
                                    .build()
                    )));
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
        } else responseSender.sendMessage(chatId, "Пользователь не найден.");
    }

    @Override
    public UserState getUserState() {
        return UserState.USER_REFERRAL_BALANCE;
    }
}
