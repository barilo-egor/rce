package tgb.btc.lib.service.processors.personaldiscounts.buy;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.User;
import tgb.btc.lib.bean.UserDiscount;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.UserDiscountRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.UserDiscountService;
import tgb.btc.lib.service.processors.support.PersonalDiscountsCache;
import tgb.btc.lib.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.PERSONAL_BUY_DISCOUNT, step = 2)
public class SavePersonalBuyDiscountProcessor extends Processor {

    private UserDiscountRepository userDiscountRepository;

    private UserDiscountService userDiscountService;

    private PersonalDiscountsCache personalDiscountsCache;

    @Autowired
    public void setPersonalDiscountsCache(PersonalDiscountsCache personalDiscountsCache) {
        this.personalDiscountsCache = personalDiscountsCache;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public void setUserDiscountService(UserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String enteredValue = UpdateUtil.getMessageText(update).replaceAll(",", ".");
        BigDecimal newPersonalBuy = BigDecimal.valueOf(Double.parseDouble(enteredValue));
        Long userChatId = Long.parseLong(userRepository.getBufferVariable(chatId));
        Long userPid = userRepository.getPidByChatId(userChatId);
        if (!userDiscountService.isExistByUserPid(userPid)) {
            UserDiscount userDiscount = new UserDiscount();
            userDiscount.setUser(new User(userPid));
            userDiscount.setPersonalBuy(newPersonalBuy);
            userDiscountRepository.save(userDiscount);
        } else userDiscountRepository.updatePersonalBuyByUserPid(newPersonalBuy, userPid);
        personalDiscountsCache.putToBuy(userChatId, newPersonalBuy);
        responseSender.sendMessage(chatId, "Персональная скидка на покупку обновлена.");
        processToAdminMainPanel(chatId);
    }

}
