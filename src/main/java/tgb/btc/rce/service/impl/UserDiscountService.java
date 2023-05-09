package tgb.btc.rce.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.service.IUserDiscountService;
import tgb.btc.rce.service.processors.support.PersonalDiscountsCache;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.CalculateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class UserDiscountService implements IUserDiscountService {
    private UserDiscountRepository userDiscountRepository;

    private PersonalDiscountsCache personalDiscountsCache;

    @Autowired
    public void setPersonalDiscountsCache(PersonalDiscountsCache personalDiscountsCache) {
        this.personalDiscountsCache = personalDiscountsCache;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Override
    public boolean isExistByUserPid(Long userPid) {
        return userDiscountRepository.countByUserPid(userPid) > 0;
    }

    private BigDecimal getPersonalByDealType(DealType dealType, Long chatId) {
        return personalDiscountsCache.getDiscount(chatId, dealType);
    }

    public void applyPersonal(Long chatId, Deal deal, DealType dealType) {
        BigDecimal personalDiscount = personalDiscountsCache.getDiscount(chatId, dealType);
        boolean isBuyDealType = DealType.BUY.equals(dealType);
        BigDecimal dealAmount = deal.getAmount();
        if (BooleanUtils.isNotTrue(deal.getPersonalApplied())) {
            if (Objects.isNull(personalDiscount)) {
                personalDiscount = getPersonalByDealType(dealType, chatId);
                if (Objects.nonNull(personalDiscount) && !BigDecimalUtil.isZero(personalDiscount)) {
                    deal.setAmount(CalculateUtil.calculateDiscount(dealType, dealAmount, personalDiscount));
                    deal.setPersonalApplied(true);
                }
            } else if (!BigDecimalUtil.isZero(personalDiscount)) {
                BigDecimal totalDiscount = CalculateUtil.getPercentsFactor(dealAmount).multiply(personalDiscount);
                deal.setAmount(isBuyDealType ? dealAmount.add(totalDiscount) : dealAmount.subtract(totalDiscount));
                deal.setPersonalApplied(true);
            }
        }
    }

}
