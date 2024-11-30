package tgb.btc.rce.service.handler.impl.state.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
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

    public ChangeReferralBalanceStateHandler(IReadUserService readUserService, IModifyUserService modifyUserService,
                                             IResponseSender responseSender, IAdminPanelService adminPanelService,
                                             IRedisUserStateService redisUserStateService,
                                             IRedisStringService redisStringService, IUserInputService userInputService) {
        this.readUserService = readUserService;
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
        this.adminPanelService = adminPanelService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.userInputService = userInputService;
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
        if (text.startsWith("+") || text.startsWith("-")) {
            Integer userReferralBalance = readUserService.getReferralBalanceByChatId(userChatId);
            if (Objects.isNull(userReferralBalance)) {
                userReferralBalance = 0;
            }
            Integer enteredSum = Integer.parseInt(text.substring(1));
            if (text.startsWith("+")) {
                total = userReferralBalance + enteredSum;
                log.info("Админ с чат айди {} добавил на баланс пользователю с чат айди {} - {} рублей. enteredSum = {}; userReferralBalance = {}; total = {}", chatId, userChatId, enteredSum, enteredSum, userReferralBalance, total);
                modifyUserService.updateReferralBalanceByChatId(total, userChatId);
                responseSender.sendMessage(userChatId, "На ваш реферальный баланс было зачислено " + Integer.parseInt(text.substring(1)) + "₽.");
            } else {
                total = userReferralBalance - enteredSum;
                log.info("Админ с чат айди {} убрал с баланса пользователю с чат айди {} - {} рублей. enteredSum = {}; userReferralBalance = {}; total = {}", chatId, userChatId, enteredSum, enteredSum, userReferralBalance, total);
                modifyUserService.updateReferralBalanceByChatId(
                        userReferralBalance
                                - enteredSum, userChatId);
                responseSender.sendMessage(userChatId, "С вашего реферального баланса списано " + Integer.parseInt(text.substring(1)) + "₽.");
            }
        } else {
            total = Integer.parseInt(text);
            log.info("Админ с чат айди {} установил баланс пользователю с чат айди {} - {} рублей", chatId, userChatId, total);
            modifyUserService.updateReferralBalanceByChatId(total, userChatId);
        }
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
