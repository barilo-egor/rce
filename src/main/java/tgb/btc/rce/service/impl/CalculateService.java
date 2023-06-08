package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.vo.calculate.CalculateData;
import tgb.btc.rce.vo.calculate.DealAmount;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CalculateService {

    private CryptoCurrencyService cryptoCurrencyService;

    @Autowired
    public void setCryptoCurrencyService(CryptoCurrencyService cryptoCurrencyService) {
        this.cryptoCurrencyService = cryptoCurrencyService;
    }

    public DealAmount calculate(BigDecimal enteredAmount, CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency,
                                DealType dealType, Boolean isEnteredInCrypto) {
        CalculateData calculateData =
                new CalculateData(fiatCurrency, dealType, cryptoCurrency, cryptoCurrencyService.getCurrency(cryptoCurrency));

        DealAmount dealAmount = new DealAmount();
        dealAmount.setDealType(dealType);
        if (Objects.nonNull(isEnteredInCrypto)) dealAmount.setEnteredInCrypto(isEnteredInCrypto);
        else dealAmount.setEnteredInCrypto(isEnteredInCrypto(cryptoCurrency, enteredAmount));
        dealAmount.setCalculateData(calculateData);
        if (dealAmount.isEnteredInCrypto()) {
            dealAmount.setCryptoAmount(enteredAmount);
            if (DealType.isBuy(dealType)) calculateAmount(dealAmount, calculateData);
            else calculateAmountForSell(dealAmount, calculateData);
        } else {
            dealAmount.setAmount(enteredAmount);
            if (DealType.isBuy(dealType)) calculateCryptoAmount(dealAmount, calculateData);
            else calculateCryptoAmountForSell(dealAmount, calculateData);
        }
        return dealAmount;
    }

    public DealAmount calculate(BigDecimal enteredAmount, CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency,
                                DealType dealType) {
        return calculate(enteredAmount, cryptoCurrency, fiatCurrency, dealType, null);
    }

    private boolean isEnteredInCrypto(CryptoCurrency cryptoCurrency, BigDecimal enteredAmount) {
        return !CryptoCurrency.BITCOIN.equals(cryptoCurrency)
                || enteredAmount.compareTo(BotVariablePropertiesUtil.getBigDecimal(BotVariableType.DEAL_BTC_MAX_ENTERED_SUM.getKey())) < 0;
    }

    private void calculateCryptoAmount(DealAmount dealAmount, CalculateData calculateData) {
        BigDecimal amount = dealAmount.getAmount();
        BigDecimal commission = amount.compareTo(calculateData.getFix()) < 0
                ? calculateData.getFixCommission()
                : BigDecimalUtil.multiplyHalfUp(amount, getPercentsFactor(calculateData.getCommission()));
        dealAmount.setCommission(commission);
        amount = amount.subtract(commission);
        BigDecimal usdCourse = calculateData.getUsdCourse();
        BigDecimal transactionCommission = calculateData.getTransactionalCommission();
        if (Objects.nonNull(transactionCommission)) {
            amount = BigDecimalUtil.subtractHalfUp(amount, BigDecimalUtil.multiplyHalfUp(transactionCommission, usdCourse));
        }
        BigDecimal usd = BigDecimalUtil.divideHalfUp(amount, usdCourse);
        dealAmount.setCryptoAmount(BigDecimalUtil.divideHalfUp(usd, calculateData.getCryptoCourse()));
    }

    private void calculateAmount(DealAmount dealAmount, CalculateData calculateData) {
        BigDecimal amount;
        BigDecimal cryptoCourse = calculateData.getCryptoCourse();
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(dealAmount.getCryptoAmount(), cryptoCourse);
        BigDecimal course = calculateData.getUsdCourse();
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        if (rub.compareTo(calculateData.getFix()) < 0){
            BigDecimal commission = rub.compareTo(calculateData.getFix()) < 0
                    ? calculateData.getFixCommission()
                    : getCommission(dealAmount.getCryptoAmount(), cryptoCourse, calculateData.getCommission(), course);
            dealAmount.setCommission(commission);
            amount = BigDecimalUtil.addHalfUp(rub, commission);
        } else {
            BigDecimal currentPercents = BigDecimalUtil.subtractHalfUp(BigDecimal.valueOf(100), calculateData.getCommission());
            BigDecimal onePercent = BigDecimalUtil.divideHalfUp(dealAmount.getCryptoAmount(), currentPercents);
            BigDecimal totalCommission = BigDecimalUtil.multiplyHalfUp(onePercent, calculateData.getCommission());
            BigDecimal usdTotalCommission = BigDecimalUtil.multiplyHalfUp(totalCommission, cryptoCourse);
            BigDecimal rubTotalCommission = BigDecimalUtil.multiplyHalfUp(usdTotalCommission, course);
            dealAmount.setCommission(rubTotalCommission);
            BigDecimal totalCryptoAmount = BigDecimalUtil.addHalfUp(dealAmount.getCryptoAmount(), totalCommission);
            usd = BigDecimalUtil.multiplyHalfUp(totalCryptoAmount, cryptoCourse);
            amount = BigDecimalUtil.multiplyHalfUp(usd, course);
        }
        BigDecimal transactionCommission = calculateData.getTransactionalCommission();
        if (Objects.nonNull(transactionCommission)) {
            amount = BigDecimalUtil.addHalfUp(amount, BigDecimalUtil.multiplyHalfUp(transactionCommission, course));
        }
        dealAmount.setAmount(amount);
    }

    private void calculateCryptoAmountForSell(DealAmount dealAmount, CalculateData calculateData) {
        BigDecimal amount = dealAmount.getAmount();
        BigDecimal commission = amount.compareTo(calculateData.getFix()) < 0
                ? calculateData.getFixCommission()
                : getCommissionForSell(amount, calculateData.getCommission());
        amount = amount.add(commission);
        BigDecimal usd = BigDecimalUtil.divideHalfUp(amount, calculateData.getUsdCourse());
        dealAmount.setCommission(commission);
        dealAmount.setCryptoAmount(BigDecimalUtil.divideHalfUp(usd, calculateData.getCryptoCourse()));
    }

    private void calculateAmountForSell(DealAmount dealAmount, CalculateData calculateData) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(dealAmount.getCryptoAmount(), calculateData.getCryptoCourse());
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, calculateData.getUsdCourse());
        if (rub.compareTo(calculateData.getFix()) < 0) {
            dealAmount.setCommission(calculateData.getFixCommission());
            dealAmount.setAmount(BigDecimalUtil.subtractHalfUp(rub, calculateData.getFixCommission()));
        } else {
            BigDecimal currentPercents = BigDecimalUtil.addHalfUp(BigDecimal.valueOf(100), calculateData.getCommission());
            BigDecimal onePercent = BigDecimalUtil.divideHalfUp(dealAmount.getCryptoAmount(), currentPercents);
            BigDecimal totalCommission = BigDecimalUtil.multiplyHalfUp(onePercent, calculateData.getCommission());
            BigDecimal usdTotalCommission = BigDecimalUtil.multiplyHalfUp(totalCommission, calculateData.getCryptoCourse());
            BigDecimal rubTotalCommission = BigDecimalUtil.multiplyHalfUp(usdTotalCommission, calculateData.getUsdCourse());
            dealAmount.setCommission(rubTotalCommission);
            BigDecimal totalCryptoAmount = BigDecimalUtil.subtractHalfUp(dealAmount.getCryptoAmount(), totalCommission);
            usd = BigDecimalUtil.multiplyHalfUp(totalCryptoAmount, calculateData.getCryptoCourse());
            dealAmount.setAmount(BigDecimalUtil.multiplyHalfUp(usd, calculateData.getUsdCourse()));
        }
    }

    public BigDecimal getCommissionForSell(BigDecimal rub, BigDecimal percentCommission) {
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    private BigDecimal getCommission(BigDecimal amount, BigDecimal cryptoCurrency, BigDecimal percentCommission,
                                     BigDecimal course) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public BigDecimal getPercentsFactor(BigDecimal sum) {
        return BigDecimalUtil.divideHalfUp(sum, BigDecimal.valueOf(100));
    }

    public BigDecimal calculateDiscountInFiat(DealType dealType, BigDecimal amount, BigDecimal discount) {
        return getPercentsFactor(amount).multiply(discount);
    }

    public BigDecimal calculateDiscountInCrypto(CalculateData calculateData, BigDecimal discountInFiat) {
        BigDecimal usd = BigDecimalUtil.divideHalfUp(discountInFiat, calculateData.getUsdCourse());
        return BigDecimalUtil.divideHalfUp(usd, calculateData.getCryptoCourse());
    }

    public BigDecimal convertToFiat(DealType dealType, CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency,
                                    BigDecimal cryptoAmount) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(cryptoAmount, cryptoCurrencyService.getCurrency(cryptoCurrency));
        return BigDecimalUtil.multiplyHalfUp(usd,
                BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency));
    }
}
