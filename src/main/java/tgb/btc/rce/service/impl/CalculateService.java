package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CalculateService {

    private CryptoCurrencyService cryptoCurrencyService;

    @Autowired
    public void setCryptoCurrencyService(CryptoCurrencyService cryptoCurrencyService) {
        this.cryptoCurrencyService = cryptoCurrencyService;
    }

    public BigDecimal convert(CryptoCurrency cryptoCurrency, Double sum, FiatCurrency fiatCurrency,
                              DealType dealType, boolean isEnteredCrypto) {
        BigDecimal fix = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal usdCourse = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal commission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal fixCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX_COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal transactionalCommission = BotVariablePropertiesUtil.getTransactionCommission(cryptoCurrency);

        BigDecimal currency = cryptoCurrencyService.getCurrency(cryptoCurrency);
        if (isEnteredCrypto) {
            if (DealType.isBuy(dealType)) return getAmount(BigDecimal.valueOf(sum), usdCourse, fix, commission,
                    fixCommission, transactionalCommission, currency);
            else return getAmountForSell(BigDecimal.valueOf(sum), usdCourse, fix, commission, fixCommission, currency);
        } else {
            if (DealType.isBuy(dealType)) return getCryptoAmount(BigDecimal.valueOf(sum), usdCourse, fix, commission,
                    fixCommission, transactionalCommission, currency);
            else return getCryptoAmountForSell(BigDecimal.valueOf(sum), usdCourse, fix, commission, fixCommission, currency);
        }
    }

    private BigDecimal getCryptoAmount(BigDecimal amount, BigDecimal course, BigDecimal fix,
                                       BigDecimal percentCommission, BigDecimal fixCommission,
                                       BigDecimal transactionCommission, BigDecimal cryptoCurrency) {
        BigDecimal commission;
        if (amount.compareTo(fix) < 0) {
            commission = fixCommission;
        } else {
            commission = BigDecimalUtil.multiplyHalfUp(amount, getPercentsFactor(percentCommission));
        }
        amount = amount.subtract(commission);
        if (Objects.nonNull(transactionCommission)) amount = BigDecimalUtil.subtractHalfUp(amount,
                BigDecimalUtil.multiplyHalfUp(transactionCommission, course));
        BigDecimal usd = BigDecimalUtil.divideHalfUp(amount, course);
        return BigDecimalUtil.divideHalfUp(usd, cryptoCurrency);
    }

    private BigDecimal getAmount(BigDecimal cryptoAmount, BigDecimal course, BigDecimal fix,
                                 BigDecimal percentCommission, BigDecimal fixCommission,
                                 BigDecimal transactionCommission, BigDecimal cryptoCurrency) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(cryptoAmount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        if (Objects.nonNull(transactionCommission)) rub = BigDecimalUtil.addHalfUp(rub,
                BigDecimalUtil.multiplyHalfUp(transactionCommission, course));
        return rub.compareTo(fix) < 0
                ? BigDecimalUtil.addHalfUp(rub, fixCommission)
                : BigDecimalUtil.addHalfUp(rub, getCommission(cryptoAmount, cryptoCurrency, percentCommission, course));
    }

    private BigDecimal getCryptoAmountForSell(BigDecimal amount, BigDecimal course, BigDecimal fix,
                                        BigDecimal percentCommission, BigDecimal fixCommission,
                                        BigDecimal cryptoCurrency) {
        BigDecimal commission;
        if (amount.compareTo(fix) < 0) {
            commission = fixCommission;
        } else {
            commission = getCommissionForSell(amount, percentCommission);
        }
        amount = amount.add(commission);
        BigDecimal usd = BigDecimalUtil.divideHalfUp(amount, course);
        return BigDecimalUtil.divideHalfUp(usd, cryptoCurrency);
    }

    private BigDecimal getAmountForSell(BigDecimal cryptoAmount, BigDecimal course, BigDecimal fix,
                                        BigDecimal percentCommission, BigDecimal fixCommission,
                                        BigDecimal cryptoCurrency) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(cryptoAmount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        return rub.compareTo(fix) < 0
                ? BigDecimalUtil.subtractHalfUp(rub, fixCommission)
                : BigDecimalUtil.subtractHalfUp(rub, getCommissionForSell(rub, percentCommission));
    }

    public BigDecimal getCommissionForSell(BigDecimal rub, BigDecimal percentCommission) {
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public BigDecimal getCommissionForSell(BigDecimal amount, CryptoCurrency cryptoCurrency,
                                                  FiatCurrency fiatCurrency, DealType dealType) {
        BigDecimal currency = cryptoCurrencyService.getCurrency(cryptoCurrency);
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, currency);
        BigDecimal course = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        BigDecimal percentCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency,
                dealType, cryptoCurrency);
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    private BigDecimal getCommission(BigDecimal amount, BigDecimal cryptoCurrency, BigDecimal percentCommission,
                                            BigDecimal course) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public BigDecimal getCommission(BigDecimal amount, CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency,
                                           DealType dealType) {
        BigDecimal fix = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal currency = cryptoCurrencyService.getCurrency(cryptoCurrency);
        BigDecimal percentCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal course = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, currency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        if (rub.doubleValue() <= fix.doubleValue()) {
            return BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX_COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        }
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public BigDecimal getPercentsFactor(BigDecimal sum) {
        return BigDecimalUtil.divideHalfUp(sum, BigDecimal.valueOf(100));
    }

    public BigDecimal calculateDiscount(DealType dealType, BigDecimal amount, BigDecimal discount) {
        BigDecimal totalDiscount = getPercentsFactor(amount).multiply(discount);
        return DealType.BUY.equals(dealType)
                ? amount.add(totalDiscount)
                : amount.subtract(totalDiscount);
    }
}