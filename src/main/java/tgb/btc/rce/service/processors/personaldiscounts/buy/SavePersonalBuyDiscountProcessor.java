package tgb.btc.rce.service.processors.personaldiscounts.buy;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.UserDiscount;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.repository.bot.UserDiscountRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserDiscountProcessService;
import tgb.btc.library.service.bean.bot.UserDiscountService;
import tgb.btc.rce.service.processors.support.PersonalDiscountsCache;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.PERSONAL_BUY_DISCOUNT, step = 2)
public class SavePersonalBuyDiscountProcessor extends Processor {

    private UserDiscountRepository userDiscountRepository;

    private UserDiscountProcessService userDiscountProcessService;

    private PersonalDiscountsCache personalDiscountsCache;

    private UserDiscountService userDiscountService;

    @Autowired
    public void setUserDiscountProcessService(UserDiscountProcessService userDiscountProcessService) {
        this.userDiscountProcessService = userDiscountProcessService;
    }

    @Autowired
    public void setUserDiscountService(UserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Autowired
    public void setPersonalDiscountsCache(PersonalDiscountsCache personalDiscountsCache) {
        this.personalDiscountsCache = personalDiscountsCache;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
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
