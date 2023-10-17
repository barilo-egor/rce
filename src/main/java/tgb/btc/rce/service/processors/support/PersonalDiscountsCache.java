package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.repository.bot.UserDiscountRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PersonalDiscountsCache {

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    private final Map<Long, BigDecimal> USERS_PERSONAL_SELL = new HashMap<>();

    private final Map<Long, BigDecimal> USERS_PERSONAL_BUY = new HashMap<>();

    public void putByDealType(DealType dealType, Long userChatId, BigDecimal personalDiscount) {
        if (DealType.isBuy(dealType)) putToBuy(userChatId, personalDiscount);
        else putToSell(userChatId, personalDiscount);
    }

    public void putToSell(Long userChatId, BigDecimal personalSell) {
        synchronized (this) {
            if (Objects.isNull(personalSell)) {
                throw new BaseException("Персональная скидка на продажу не может быть null.");
            }
            USERS_PERSONAL_SELL.put(userChatId, personalSell);
        }
    }

    public void putToBuy(Long userChatId, BigDecimal personalBuy) {
        synchronized (this) {
            if (Objects.isNull(personalBuy)) {
                throw new BaseException("Персональная скидка на покупку не может быть null.");
            }
            USERS_PERSONAL_BUY.put(userChatId, personalBuy);
        }
    }

    public BigDecimal getDiscount(Long chatId, DealType dealType) {
        synchronized (this) {
            boolean isBuy = DealType.isBuy(dealType);
            Map<Long, BigDecimal> USERS_PERSONAL = isBuy ? USERS_PERSONAL_BUY : USERS_PERSONAL_SELL;
            BigDecimal discount = USERS_PERSONAL.get(chatId);
            if (Objects.nonNull(discount)) return discount;
            BigDecimal actualDiscount = isBuy
                    ? userDiscountRepository.getPersonalBuyByChatId(chatId)
                    : userDiscountRepository.getPersonalSellByChatId(chatId);
            if (Objects.isNull(actualDiscount)) actualDiscount = BigDecimal.ZERO;
            putByDealType(dealType, chatId, actualDiscount);
            return actualDiscount;
        }
    }
}
