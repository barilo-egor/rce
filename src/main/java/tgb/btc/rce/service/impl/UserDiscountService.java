package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.service.processors.support.PersonalDiscountsCache;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BulkDiscountUtil;
import tgb.btc.rce.vo.calculate.DealAmount;

import java.math.BigDecimal;

@Service
public class UserDiscountService {
    private UserDiscountRepository userDiscountRepository;

    private PersonalDiscountsCache personalDiscountsCache;

    private CalculateService calculateService;

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setPersonalDiscountsCache(PersonalDiscountsCache personalDiscountsCache) {
        this.personalDiscountsCache = personalDiscountsCache;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    public boolean isExistByUserPid(Long userPid) {
        return userDiscountRepository.countByUserPid(userPid) > 0;
    }

    public void applyPersonal(Long chatId, DealType dealType, DealAmount dealAmount) {
        BigDecimal personalDiscount = personalDiscountsCache.getDiscount(chatId, dealType);
        if (BigDecimal.ZERO.compareTo(personalDiscount) == 0) return;
        applyDiscount(dealType, dealAmount, personalDiscount);
    }

    public void applyBulk(FiatCurrency fiatCurrency, DealType dealType, DealAmount dealAmount) {
        if (!DealType.isBuy(dealType)) return;
        BigDecimal bulkDiscount = BulkDiscountUtil.getPercentBySum(dealAmount.getAmount(), fiatCurrency);
        if (BigDecimalUtil.isZero(bulkDiscount)) return;
        applyDiscount(dealType, dealAmount, bulkDiscount);
    }

    private void applyDiscount(DealType dealType, DealAmount dealAmount, BigDecimal discount) {
        BigDecimal totalDiscount = calculateService.calculateDiscountInFiat(dealType, dealAmount.getAmount(), discount);
        if (dealAmount.isEnteredInCrypto()) {
            BigDecimal newAmount = DealType.isBuy(dealType)
                    ? dealAmount.getAmount().subtract(totalDiscount)
                    : dealAmount.getAmount().add(totalDiscount);
            dealAmount.setAmount(newAmount);
        } else {
            BigDecimal discountInCrypto = calculateService.calculateDiscountInCrypto(dealAmount.getCalculateData(), totalDiscount);
            BigDecimal newCryptoAmount = DealType.isBuy(dealType)
                    ? dealAmount.getCryptoAmount().add(discountInCrypto)
                    : dealAmount.getCryptoAmount().subtract(discountInCrypto);
            dealAmount.setCryptoAmount(newCryptoAmount);
        }
    }
}
