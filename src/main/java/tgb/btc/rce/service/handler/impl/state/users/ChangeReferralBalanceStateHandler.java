package tgb.btc.rce.service.handler.impl.state.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.BalanceAuditType;
import tgb.btc.library.interfaces.service.bean.bot.IBalanceAuditService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.util.Objects;

@Service
@Slf4j
public class ChangeReferralBalanceStateHandler implements IStateHandler {

    private final IReadUserService readUserService;

    private final IModifyUserService modifyUserService;

    private final IResponseSender responseSender;

    private final IAdminPanelService adminPanelService;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IUserInputService userInputService;

    private final IBalanceAuditService balanceAuditService;

    public ChangeReferralBalanceStateHandler(IReadUserService readUserService, IModifyUserService modifyUserService,
                                             IResponseSender responseSender, IAdminPanelService adminPanelService,
                                             IRedisUserStateService redisUserStateService,
                                             IRedisStringService redisStringService, IUserInputService userInputService,
                                             IBalanceAuditService balanceAuditService) {
        this.readUserService = readUserService;
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
        this.adminPanelService = adminPanelService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.userInputService = userInputService;
        this.balanceAuditService = balanceAuditService;
    }

    @Override
    public void handle(Update update) {
        if (!userInputService.hasTextInput(update)) {
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        Long userChatId = Long.parseLong(redisStringService.get(chatId));
        int total;

        int amount;
        BalanceAuditType type;
        if (text.startsWith("+") || text.startsWith("-")) {
            Integer userReferralBalance = readUserService.getReferralBalanceByChatId(userChatId);
            if (Objects.isNull(userReferralBalance)) {
                userReferralBalance = 0;
            }
            Integer enteredSum = Integer.parseInt(text.substring(1));
            if (text.startsWith("+")) {
                total = userReferralBalance + enteredSum;
                modifyUserService.updateReferralBalanceByChatId(total, userChatId);
                responseSender.sendMessage(userChatId, "На ваш реферальный баланс было зачислено " + Integer.parseInt(text.substring(1)) + "₽.");
                type = BalanceAuditType.MANUAL_ADDITION;
            } else {
                total = userReferralBalance - enteredSum;
                modifyUserService.updateReferralBalanceByChatId(
                        userReferralBalance
                                - enteredSum, userChatId);
                responseSender.sendMessage(userChatId, "С вашего реферального баланса списано " + Integer.parseInt(text.substring(1)) + "₽.");
                type = BalanceAuditType.MANUAL_DEBITING;
            }
            amount = enteredSum;
        } else {
            total = Integer.parseInt(text);
            modifyUserService.updateReferralBalanceByChatId(total, userChatId);
            amount = total;
            type = BalanceAuditType.MANUAL;
        }
        balanceAuditService.save(readUserService.findByChatId(userChatId), readUserService.findByChatId(chatId), amount, type);
        responseSender.sendMessage(chatId, "Баланс успешно обновлен. Текущее новое значение: <b>" + total + "₽</b>.");
        redisUserStateService.delete(chatId);
        redisStringService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.CHANGE_REFERRAL_BALANCE;
    }
}
