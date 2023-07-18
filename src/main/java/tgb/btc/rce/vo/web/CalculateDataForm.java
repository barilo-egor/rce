package tgb.btc.rce.vo.web;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;

import java.math.BigDecimal;

@Data
@Builder
public class CalculateDataForm {

    @Getter
    @Setter
    private BigDecimal usdCourse;

    @Getter
    @Setter
    private BigDecimal cryptoAmount;

    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private FiatCurrency fiatCurrency;

    @Getter
    @Setter
    private CryptoCurrency cryptoCurrency;

    @Getter
    @Setter
    private DealType dealType;

    @Getter
    @Setter
    private BigDecimal personalDiscount;

    @Getter
    @Setter
    private BigDecimal bulkDiscount;

    @Getter
    @Setter
    private BigDecimal cryptoCourse;
}
