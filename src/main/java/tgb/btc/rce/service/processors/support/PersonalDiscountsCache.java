package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.UserDiscountRepository;

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

    public void putToUsersPersonalSell(Long userChatId, BigDecimal personalSell) {
        synchronized (this) {
            if (Objects.isNull(personalSell)) {
                throw new BaseException("Персональная скидка на продажу не может быть null.");
            }
            USERS_PERSONAL_SELL.put(userChatId, personalSell);
        }
    }

    public void putToUsersPersonalBuy(Long userChatId, BigDecimal personalBuy) {
        synchronized (this) {
            if (Objects.isNull(personalBuy)) {
                throw new BaseException("Персональная скидка на покупку не может быть null.");
            }
            USERS_PERSONAL_BUY.put(userChatId, personalBuy);
        }
    }

    public BigDecimal getBuyDiscount(Long chatId) {
        return getDiscount(chatId, DealType.BUY);
    }

    public BigDecimal getSellDiscount(Long chatId) {
        return getDiscount(chatId, DealType.SELL);
    }

    public BigDecimal getDiscount(Long chatId, DealType dealType) {
        synchronized (this) {
            BigDecimal discount;
            if (DealType.BUY.equals(dealType)) {
                discount = USERS_PERSONAL_BUY.get(chatId);
                if (Objects.isNull(discount)) {
                    BigDecimal actualDiscount = userDiscountRepository.getPersonalBuyByChatId(chatId);
                    if (Objects.isNull(actualDiscount)) actualDiscount = BigDecimal.ZERO;
                    putToUsersPersonalBuy(chatId, actualDiscount);
                }
                return USERS_PERSONAL_BUY.get(chatId);
            } else {
                return USERS_PERSONAL_SELL.get(chatId);
            }
        }
    }
}
