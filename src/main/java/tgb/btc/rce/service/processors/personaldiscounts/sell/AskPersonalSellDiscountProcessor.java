package tgb.btc.rce.service.processors.personaldiscounts.sell;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@CommandProcessor(command = Command.PERSONAL_SELL_DISCOUNT, step = 1)
public class AskPersonalSellDiscountProcessor extends Processor {

    private UserRepository userRepository;

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public AskPersonalSellDiscountProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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
