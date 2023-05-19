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
import tgb.btc.rce.util.BulkDiscountUtil;
import tgb.btc.rce.util.CalculateUtil;

import java.math.BigDecimal;

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

    @Override
    public void applyPersonal(Long chatId, Deal deal) {
        if (BooleanUtils.isTrue(deal.getPersonalApplied())) return;
        DealType dealType = deal.getDealType();
        BigDecimal personalDiscount = personalDiscountsCache.getDiscount(chatId, dealType);
        if (!BigDecimalUtil.isZero(personalDiscount)) {
            deal.setAmount(CalculateUtil.calculateDiscount(dealType, deal.getAmount(), personalDiscount));
            deal.setPersonalApplied(true);
        }
    }

    @Override
    public void applyBulk(Deal deal) {
        DealType dealType = deal.getDealType();
        if (!DealType.isBuy(dealType)) return;
        BigDecimal bulkDiscount = BulkDiscountUtil.getPercentBySum(deal.getAmount(), deal.getFiatCurrency());
        if (!BigDecimalUtil.isZero(bulkDiscount))
            deal.setAmount(CalculateUtil.calculateDiscount(dealType, deal.getAmount(), bulkDiscount));
    }
}
