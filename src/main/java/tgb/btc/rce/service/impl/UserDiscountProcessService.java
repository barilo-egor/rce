package tgb.btc.rce.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.repository.bot.UserDiscountRepository;
import tgb.btc.library.repository.bot.UserRepository;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.rce.enums.Rank;
import tgb.btc.rce.enums.ReferralType;
import tgb.btc.rce.enums.VariableType;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.math.BigDecimal;

@Service
public class UserDiscountProcessService {
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

    public BigDecimal applyDealDiscounts(Long chatId, BigDecimal dealAmount, Boolean isUsedPromo,
                                         Boolean isUserReferralDiscount, BigDecimal discount, FiatCurrency fiatCurrency) {
        BigDecimal newDealAmount = applyPromoCodeDiscount(dealAmount, isUsedPromo, discount);
        newDealAmount = applyReferralDiscount(chatId, newDealAmount, isUserReferralDiscount, fiatCurrency);
        return newDealAmount;
    }

    private BigDecimal applyPromoCodeDiscount(BigDecimal dealAmount, Boolean isUsedPromo, BigDecimal discount) {
        if (BooleanUtils.isTrue(isUsedPromo)) {
            dealAmount = dealAmount.subtract(discount);
        }
        return dealAmount;
    }

    private BigDecimal applyReferralDiscount(Long chatId, BigDecimal dealAmount, Boolean isUsedReferralDiscount, FiatCurrency fiatCurrency) {
        if (BooleanUtils.isTrue(isUsedReferralDiscount)) {
            Integer referralBalance = userRepository.getReferralBalanceByChatId(chatId);
            if (ReferralType.CURRENT.isCurrent() && !FiatCurrency.BYN.equals(fiatCurrency)) {
                if (referralBalance <= dealAmount.intValue())
                    dealAmount = dealAmount.subtract(BigDecimal.valueOf(referralBalance));
                else dealAmount = BigDecimal.ZERO;
            } else {
                if (CommonProperties.VARIABLE.isNotBlank("course.rub.byn")) {
                    BigDecimal bynReferralBalance = BigDecimal.valueOf(referralBalance).multiply(CommonProperties.VARIABLE.getBigDecimal("course.rub.byn"));
                    if (bynReferralBalance.compareTo(dealAmount) < 1)
                        dealAmount = dealAmount.subtract(bynReferralBalance);
                    else dealAmount = BigDecimal.ZERO;
                } else {
                    BigDecimal refBalance = BigDecimal.valueOf(referralBalance);
                    if (refBalance.compareTo(dealAmount) < 1)
                        dealAmount = dealAmount.subtract(refBalance);
                    else dealAmount = BigDecimal.ZERO;
                }
            }
        }
        return dealAmount;
    }

    public BigDecimal applyRank(Rank rank, Deal deal) {
        BigDecimal newAmount = deal.getAmount();
        boolean isRankDiscountOn = BooleanUtils.isTrue(
                BotVariablePropertiesUtil.getBoolean(VariableType.DEAL_RANK_DISCOUNT_ENABLE))
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
