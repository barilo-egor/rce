package tgb.btc.rce.service.processors.personaldiscounts.buy;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.repository.bot.UserDiscountRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@CommandProcessor(command = Command.PERSONAL_BUY_DISCOUNT, step = 1)
public class AskPersonalBuyDiscountProcessor extends Processor {

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
        BigDecimal personalBuy = userDiscountRepository.getPersonalBuyByChatId(userChatId);
        userRepository.updateBufferVariable(chatId, userChatId.toString());
        if (Objects.isNull(personalBuy)) personalBuy = BigDecimal.ZERO;

        responseSender.sendMessage(chatId, "У пользователя " + userChatId + " текущее значение скидки на покупку = "
                + personalBuy.stripTrailingZeros().toPlainString());
        responseSender.sendMessage(chatId, "Введите положительное значение для скидки, либо отрицательное для надбавки.");
        userRepository.nextStep(chatId);
    }

}
