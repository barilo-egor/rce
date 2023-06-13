package tgb.btc.rce.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.Rank;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.math.BigDecimal;

@Service
public class UserDiscountService {
    private UserDiscountRepository userDiscountRepository;

    private CalculateService calculateService;

    private UserRepository userRepository;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    public boolean isExistByUserPid(Long userPid) {
        return userDiscountRepository.countByUserPid(userPid) > 0;
    }

    public BigDecimal applyDealDiscounts(Long chatId, BigDecimal dealAmount, Boolean isUsedPromo,
                                         Boolean isUserReferralDiscount, BigDecimal discount) {
        BigDecimal newDealAmount = applyPromoCodeDiscount(dealAmount, isUsedPromo, discount);
        newDealAmount = applyReferralDiscount(chatId, newDealAmount, isUserReferralDiscount);
        return newDealAmount;
    }

    private BigDecimal applyPromoCodeDiscount(BigDecimal dealAmount, Boolean isUsedPromo, BigDecimal discount) {
        if (BooleanUtils.isTrue(isUsedPromo)) {
            dealAmount = dealAmount.subtract(discount);
        }
        return dealAmount;
    }

    private BigDecimal applyReferralDiscount(Long chatId, BigDecimal dealAmount, Boolean isUsedReferralDiscount) {
        if (BooleanUtils.isTrue(isUsedReferralDiscount)) {
            Integer referralBalance = userRepository.getReferralBalanceByChatId(chatId);
            if (referralBalance <= dealAmount.intValue())
                dealAmount = dealAmount.subtract(BigDecimal.valueOf(referralBalance));
            else dealAmount = BigDecimal.ZERO;
        }
        return dealAmount;
    }

    public BigDecimal applyRank(Rank rank, Deal deal) {
        BigDecimal newAmount = deal.getAmount();
        boolean isRankDiscountOn = BooleanUtils.isTrue(
                BotVariablePropertiesUtil.getBoolean(BotVariableType.DEAL_RANK_DISCOUNT_ENABLE))
                && BooleanUtils.isNotFalse(userDiscountRepository.getRankDiscountByUserChatId(
                        dealRepository.getUserChatIdByDealPid(deal.getPid())));
        if (!Rank.FIRST.equals(rank) && isRankDiscountOn) {
            BigDecimal commission = deal.getCommission();
            BigDecimal rankDiscount = BigDecimalUtil.multiplyHalfUp(commission, calculateService.getPercentsFactor(
                    BigDecimal.valueOf(rank.getPercent())));
            newAmount = DealType.isBuy(deal.getDealType())
                    ? BigDecimalUtil.subtractHalfUp(deal.getAmount(), rankDiscount)
                    : BigDecimalUtil.addHalfUp(deal.getAmount(), rankDiscount);
        }
        return newAmount;
    }
}
