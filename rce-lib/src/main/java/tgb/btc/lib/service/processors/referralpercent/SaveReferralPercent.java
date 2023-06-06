package tgb.btc.lib.service.processors.referralpercent;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.REFERRAL_PERCENT, step = 2)
public class SaveReferralPercent extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String enteredValue = UpdateUtil.getMessageText(update).replaceAll(",", ".");
        BigDecimal newReferralPercent = BigDecimal.valueOf(Double.parseDouble(enteredValue));
        Long userChatId = Long.parseLong(userRepository.getBufferVariable(chatId));
        userRepository.updateReferralPercent(newReferralPercent, userChatId);
        responseSender.sendMessage(chatId, "Процент по реферальным отчислениям обновлен.");
        processToAdminMainPanel(chatId);
    }
}
