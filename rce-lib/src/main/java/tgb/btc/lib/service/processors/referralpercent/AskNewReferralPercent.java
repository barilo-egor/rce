package tgb.btc.lib.service.processors.referralpercent;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@CommandProcessor(command = Command.REFERRAL_PERCENT, step = 1)
public class AskNewReferralPercent extends Processor {

    @Override
    public void run(Update update) {
        Long userChatId = UpdateUtil.getLongFromText(update);
        Long chatId = UpdateUtil.getChatId(update);
        if (!userRepository.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        userRepository.updateBufferVariable(chatId, userChatId.toString());
        BigDecimal referralPercent = userRepository.getReferralPercentByChatId(userChatId);
        if (Objects.isNull(referralPercent)) referralPercent = BigDecimal.ZERO;

        responseSender.sendMessage(chatId, "У пользователя " + userChatId + " текущее значение процента по реферальным отчислениям = "
                + referralPercent.stripTrailingZeros().toPlainString());
        responseSender.sendMessage(chatId, "Введите новый процент по реферальным отчислениям. Для того, чтобы у пользователя был общий" +
                " процент, введите 0.");
        userRepository.nextStep(chatId);
    }
}
