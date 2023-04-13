package tgb.btc.rce.service.processors.personaldiscounts.buy;

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

@CommandProcessor(command = Command.PERSONAL_BUY_DISCOUNT, step = 1)
public class AskPersonalBuyDiscountProcessor extends Processor {

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
    public AskPersonalBuyDiscountProcessor(IResponseSender responseSender, UserService userService) {
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
        BigDecimal personalBuy = userDiscountRepository.getPersonalBuyByChatId(userChatId);
        userRepository.updateBufferVariable(chatId, userChatId.toString());

        responseSender.sendMessage(chatId, "У пользователя " + userChatId + " текущее значение скидки на покупку = "
                + personalBuy.stripTrailingZeros().toPlainString());
        responseSender.sendMessage(chatId, "Введите отрицательное значение для скидки, либо положительное для надбавки.");
        userRepository.nextStep(chatId);
    }

}
