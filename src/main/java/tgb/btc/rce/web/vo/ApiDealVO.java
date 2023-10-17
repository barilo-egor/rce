package tgb.btc.rce.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.web.controller.api.enums.StatusCode;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
public class ApiDealVO {

    private String token;

    private DealType dealType;

    private BigDecimal amount;

    private BigDecimal cryptoAmount;

    private CryptoCurrency cryptoCurrency;

    private String requisite;

    private FiatCurrency fiatCurrency;

    public StatusCode verify() {
        if (Objects.isNull(token)) return StatusCode.EMPTY_TOKEN;
        if (Objects.isNull(dealType)) return StatusCode.EMPTY_DEAL_TYPE;
        if (Objects.isNull(amount) && Objects.isNull(cryptoAmount)) return StatusCode.EMPTY_AMOUNTS;
        if (Objects.nonNull(amount) && Objects.nonNull(cryptoAmount)) return StatusCode.ONLY_ONE_AMOUNT_NEEDED;
        if (Objects.isNull(cryptoCurrency)) return StatusCode.EMPTY_CRYPTO_CURRENCY;
        if (Objects.isNull(requisite)) return StatusCode.EMPTY_REQUISITE;
        return null;
    }
}
