package tgb.btc.rce.vo;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.CalculatorQueryException;
import tgb.btc.library.exception.EnumTypeNotFoundException;

import java.math.BigDecimal;

public class CalculatorQuery {
    private final BigDecimal enteredAmount;

    private final FiatCurrency fiatCurrency;

    private final DealType dealType;

    private final CryptoCurrency currency;


    public CalculatorQuery(String query) {
        String[] queryValues = query.split(" ");
        if (queryValues.length < 2) throw new CalculatorQueryException();
        String[] settings = queryValues[0].split("-");
        if (settings.length < 3) throw new CalculatorQueryException();
        try {
            this.enteredAmount = BigDecimal.valueOf(Double.parseDouble(queryValues[1]));
            this.fiatCurrency = FiatCurrency.getByCode(settings[0]);
            this.dealType = DealType.findByKey(settings[1]);
            this.currency = CryptoCurrency.fromShortName(settings[2]);
        } catch (EnumTypeNotFoundException | NumberFormatException e) {
            throw new CalculatorQueryException();
        }
    }

    public BigDecimal getEnteredAmount() {
        return enteredAmount;
    }

    public CryptoCurrency getCurrency() {
        return currency;
    }

    public DealType getDealType() {
        return dealType;
    }

    public FiatCurrency getFiatCurrency() {
        return fiatCurrency;
    }
}
