package tgb.btc.rce.vo.web;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;

@Data
public class CalculateDataForm {

    @Getter
    @Setter
    private Double course;

    @Getter
    @Setter
    private Double value;

    @Getter
    @Setter
    private FiatCurrency fiatCurrency;

    @Getter
    @Setter
    private CryptoCurrency cryptoCurrency;

    @Getter
    @Setter
    private DealType dealType;
}
