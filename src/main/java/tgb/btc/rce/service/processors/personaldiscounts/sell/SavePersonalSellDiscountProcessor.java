package tgb.btc.rce.service.processors.personaldiscounts.sell;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.UserDiscount;
import tgb.btc.library.repository.bot.UserDiscountRepository;
import tgb.btc.library.service.bean.bot.UserDiscountService;
import tgb.btc.library.service.process.PersonalDiscountsCache;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.PERSONAL_SELL_DISCOUNT, step = 2)
public class SavePersonalSellDiscountProcessor extends Processor {

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
        BigDecimal newPersonalSell = BigDecimal.valueOf(Double.parseDouble(enteredValue));
        Long userChatId = Long.parseLong(userRepository.getBufferVariable(chatId));
        Long userPid = userRepository.getPidByChatId(userChatId);
        if (!userDiscountService.isExistByUserPid(userPid)) {
            UserDiscount userDiscount = new UserDiscount();
            userDiscount.setUser(new User(userPid));
            userDiscount.setPersonalSell(newPersonalSell);
            userDiscountRepository.save(userDiscount);
        } else userDiscountRepository.updatePersonalSellByUserPid(newPersonalSell, userPid);
        personalDiscountsCache.putToSell(userChatId, newPersonalSell);
        responseSender.sendMessage(chatId, "Персональная скидка на продажу обновлена.");
        processToAdminMainPanel(chatId);
    }

}
