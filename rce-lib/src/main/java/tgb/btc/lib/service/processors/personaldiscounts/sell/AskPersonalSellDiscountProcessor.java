package tgb.btc.lib.service.processors.personaldiscounts.sell;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.UserDiscountRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@CommandProcessor(command = Command.PERSONAL_SELL_DISCOUNT, step = 1)
public class AskPersonalSellDiscountProcessor extends Processor {

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Override
    public void run(Update update) {
        Long userChatId = UpdateUtil.getLongFromText(update);
        Long chatId = UpdateUtil.getChatId(update);
        if (!userRepository.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        BigDecimal personalSell = userDiscountRepository.getPersonalSellByChatId(userChatId);
        userRepository.updateBufferVariable(chatId, userChatId.toString());
        if (Objects.isNull(personalSell)) personalSell = BigDecimal.ZERO;

        responseSender.sendMessage(chatId, "У пользователя " + userChatId + " текущее значение скидки на продажу = "
                + personalSell.stripTrailingZeros().toPlainString());
        responseSender.sendMessage(chatId, "Введите отрицательное значение для скидки, либо положительное для надбавки.");
        userRepository.nextStep(chatId);
    }

}
