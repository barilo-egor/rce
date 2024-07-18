package tgb.btc.rce.service.impl.process;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.process.CalculateService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.rce.enums.Rank;
import tgb.btc.rce.service.process.IUserDiscountProcessService;

import java.math.BigDecimal;

@Service
public class UserDiscountProcessService implements IUserDiscountProcessService {

    private IUserDiscountService userDiscountService;

    private CalculateService calculateService;

    private IReadUserService readUserService;

    private IDealUserService dealUserService;

    private VariablePropertiesReader variablePropertiesReader;

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setDealUserService(IDealUserService dealUserService) {
        this.dealUserService = dealUserService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setUserDiscountService(IUserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Override
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
            Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
            if (ReferralType.CURRENT.isCurrent() && !FiatCurrency.BYN.equals(fiatCurrency)) {
                if (referralBalance <= dealAmount.intValue())
                    dealAmount = dealAmount.subtract(BigDecimal.valueOf(referralBalance));
                else dealAmount = BigDecimal.ZERO;
            } else {
                if (PropertiesPath.VARIABLE_PROPERTIES.isNotBlank("course.rub.byn")) {
                    BigDecimal bynReferralBalance = BigDecimal.valueOf(referralBalance).multiply(PropertiesPath.VARIABLE_PROPERTIES.getBigDecimal("course.rub.byn"));
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

    @Override
    public BigDecimal applyRank(Rank rank, Deal deal) {
        BigDecimal newAmount = deal.getAmount();
        boolean isRankDiscountOn = BooleanUtils.isTrue(
                variablePropertiesReader.getBoolean(VariableType.DEAL_RANK_DISCOUNT_ENABLE))
                && BooleanUtils.isNotFalse(userDiscountService.getRankDiscountByUserChatId(
                dealUserService.getUserChatIdByDealPid(deal.getPid())));
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
