package tgb.btc.rce.service.processors.referralpercent;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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
