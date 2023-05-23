package tgb.btc.rce.vo;

import lombok.Builder;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.math.BigDecimal;

public class CalculateData {
    private BigDecimal fix;

    private BigDecimal usdCourse;

    private BigDecimal commission;

    private BigDecimal fixCommission;

    private BigDecimal transactionalCommission;

    private BigDecimal cryptoCourse;

    public CalculateData(FiatCurrency fiatCurrency, DealType dealType, CryptoCurrency cryptoCurrency, BigDecimal cryptoCourse) {
        this.fix = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX, fiatCurrency, dealType, cryptoCurrency);
        this.usdCourse = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        this.commission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        this.fixCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX_COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        this.transactionalCommission = BotVariablePropertiesUtil.getTransactionCommission(cryptoCurrency);
        this.cryptoCourse = cryptoCourse;
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

}
