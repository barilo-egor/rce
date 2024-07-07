package tgb.btc.rce.service.processors.admin.settings.disounts.referralpercent;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.REFERRAL_PERCENT, step = 2)
@Slf4j
public class SaveReferralPercent extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String enteredValue = UpdateUtil.getMessageText(update).replaceAll(",", ".");
        BigDecimal newReferralPercent = BigDecimal.valueOf(Double.parseDouble(enteredValue));
        Long userChatId = Long.parseLong(readUserService.getBufferVariable(chatId));
        modifyUserService.updateReferralPercent(newReferralPercent, userChatId);
        responseSender.sendMessage(chatId, "Процент по реферальным отчислениям обновлен.");
        log.debug("Админ chatId={} обновил процент по реферальным отчислениям={} .", chatId, BigDecimalUtil.roundToPlainString(newReferralPercent, 2));
        processToAdminMainPanel(chatId);
    }
}
