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

@CommandProcessor(command = Command.PERSONAL_BUY_DISCOUNT, step = 2)
public class SavePersonalBuyDiscountProcessor extends Processor {

    private UserRepository userRepository;

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public SavePersonalBuyDiscountProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String enteredValue = UpdateUtil.getMessageText(update).replaceAll(",", ".");
        BigDecimal newPersonalBuy = BigDecimal.valueOf(Double.parseDouble(enteredValue));
        Long userChatId = Long.parseLong(userRepository.getBufferVariable(chatId));
        userDiscountRepository.updatePersonalBuyByChatId(newPersonalBuy, userChatId);
        responseSender.sendMessage(chatId, "Персональная скидка на покупку обновлена.");
        processToAdminMainPanel(chatId);
    }

}
