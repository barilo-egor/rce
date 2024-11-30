package tgb.btc.rce.service.handler.impl.state.disount;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.math.BigDecimal;

@Service
public class NewReferralPercentHandler implements IStateHandler {

    private final IUserInputService userInputService;

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IModifyUserService modifyUserService;

    private final IAdminPanelService adminPanelService;

    private final IBigDecimalService bigDecimalService;

    public NewReferralPercentHandler(IUserInputService userInputService, IResponseSender responseSender,
                                     IRedisStringService redisStringService, IRedisUserStateService redisUserStateService,
                                     IModifyUserService modifyUserService, IAdminPanelService adminPanelService,
                                     IBigDecimalService bigDecimalService) {
        this.userInputService = userInputService;
        this.responseSender = responseSender;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.modifyUserService = modifyUserService;
        this.adminPanelService = adminPanelService;
        this.bigDecimalService = bigDecimalService;
    }

    @Override
    public void handle(Update update) {
        if (!userInputService.hasTextInput(update)) {
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Long userChatId = Long.parseLong(redisStringService.get(message.getChatId()));
        String enteredValue = message.getText();
        BigDecimal referralPercent;
        try {
            referralPercent = BigDecimal.valueOf(Double.parseDouble(enteredValue));
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Введите валидное значение.");
            return;
        }
        modifyUserService.updateReferralPercent(referralPercent, userChatId);
        responseSender.sendMessage(chatId, "Значение реферальных отчислений пользователя <b>" + userChatId
                + "</b> обновлено до <b>" + bigDecimalService.roundToPlainString(referralPercent, 2) + "</b>%.");
        redisUserStateService.delete(chatId);
        redisStringService.delete(userChatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.NEW_REFERRAL_PERCENT;
    }
}
