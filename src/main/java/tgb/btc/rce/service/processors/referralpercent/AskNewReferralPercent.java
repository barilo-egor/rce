package tgb.btc.rce.service.processors.referralpercent;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@CommandProcessor(command = Command.REFERRAL_PERCENT, step = 1)
public class AskNewReferralPercent extends Processor {

    @Override
    public void run(Update update) {
        Long userChatId = UpdateUtil.getLongFromText(update);
        Long chatId = UpdateUtil.getChatId(update);
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        modifyUserService.updateBufferVariable(chatId, userChatId.toString());
        BigDecimal referralPercent = readUserService.getReferralPercentByChatId(userChatId);
        if (Objects.isNull(referralPercent)) referralPercent = BigDecimal.ZERO;

        responseSender.sendMessage(chatId, "У пользователя " + userChatId + " текущее значение процента по реферальным отчислениям = "
                + referralPercent.stripTrailingZeros().toPlainString());
        responseSender.sendMessage(chatId, "Введите новый процент по реферальным отчислениям. Для того, чтобы у пользователя был общий" +
                " процент, введите 0.");
        modifyUserService.nextStep(chatId);
    }
}
