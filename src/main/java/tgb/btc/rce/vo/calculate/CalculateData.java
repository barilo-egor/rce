package tgb.btc.rce.vo.calculate;

import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.math.BigDecimal;

public class CalculateData {
    private final BigDecimal fix;

    private final BigDecimal usdCourse;

    private final BigDecimal commission;

    private final BigDecimal fixCommission;

    private final BigDecimal transactionalCommission;

    private final BigDecimal cryptoCourse;

    private BigDecimal personalDiscount;

    private BigDecimal bulkDiscount;

    public CalculateData(FiatCurrency fiatCurrency, DealType dealType, CryptoCurrency cryptoCurrency, BigDecimal cryptoCourse) {
        this.fix = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX, fiatCurrency, dealType, cryptoCurrency);
        this.usdCourse = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        this.commission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        this.fixCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX_COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        this.transactionalCommission = BotVariablePropertiesUtil.getTransactionCommission(cryptoCurrency);
        this.cryptoCourse = cryptoCourse;
    }

    public CalculateData(FiatCurrency fiatCurrency, DealType dealType, CryptoCurrency cryptoCurrency, BigDecimal cryptoCourse,
                         BigDecimal usdCourse, BigDecimal personalDiscount, BigDecimal bulkDiscount) {
        this.fix = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX, fiatCurrency, dealType, cryptoCurrency);
        this.usdCourse = usdCourse;
        this.commission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        this.fixCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX_COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        this.transactionalCommission = BotVariablePropertiesUtil.getTransactionCommission(cryptoCurrency);
        this.cryptoCourse = cryptoCourse;
        this.personalDiscount = personalDiscount;
        this.bulkDiscount = bulkDiscount;
    }

    public BigDecimal getFix() {
        return fix;
    }

    public BigDecimal getUsdCourse() {
        return usdCourse;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public BigDecimal getFixCommission() {
        return fixCommission;
    }

    public BigDecimal getTransactionalCommission() {
        return transactionalCommission;
    }

    public BigDecimal getCryptoCourse() {
        return cryptoCourse;
    }

    public BigDecimal getPersonalDiscount() {
        return personalDiscount;
    }

    public BigDecimal getBulkDiscount() {
        return bulkDiscount;
    }
}
