package tgb.btc.rce.service.handler.impl.state.disount;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class ReferralPercentStateHandler implements IStateHandler {

    private final IUserInputService userInputService;

    private final IResponseSender responseSender;

    private final IReadUserService readUserService;

    private final IBigDecimalService bigDecimalService;

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    public ReferralPercentStateHandler(IUserInputService userInputService, IResponseSender responseSender,
                                       IReadUserService readUserService, IBigDecimalService bigDecimalService,
                                       ICallbackDataService callbackDataService, IRedisUserStateService redisUserStateService,
                                       IAdminPanelService adminPanelService) {
        this.userInputService = userInputService;
        this.responseSender = responseSender;
        this.readUserService = readUserService;
        this.bigDecimalService = bigDecimalService;
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
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
        BigDecimal referralPercent = readUserService.getReferralPercentByChatId(userChatId);
        if (Objects.isNull(referralPercent)) referralPercent = BigDecimal.ZERO;
        responseSender.sendMessage(chatId, "У пользователя <b>" + userChatId
                + "</b> текущий процент реферальных отчислений: <b>" + bigDecimalService.roundToPlainString(referralPercent, 2)
                + "</b>%.",
                InlineButton.builder()
                        .text("Изменить")
                        .data(callbackDataService.buildData(CallbackQueryData.REFERRAL_PERCENT, userChatId))
                        .build(),
                InlineButton.builder()
                        .text("Отмена")
                        .data(CallbackQueryData.INLINE_DELETE.name())
                        .build()
        );
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.REFERRAL_PERCENT;
    }
}
