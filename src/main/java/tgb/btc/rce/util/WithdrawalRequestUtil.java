package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.constants.enums.bot.WithdrawalRequestStatus;

public final class WithdrawalRequestUtil {
    private WithdrawalRequestUtil() {
    }

    public static WithdrawalRequest buildFromUpdate(User user, Update update) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUser(user);
        withdrawalRequest.setStatus(WithdrawalRequestStatus.CREATED);
        withdrawalRequest.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
        withdrawalRequest.setActive(true);
        return withdrawalRequest;
    }
}
