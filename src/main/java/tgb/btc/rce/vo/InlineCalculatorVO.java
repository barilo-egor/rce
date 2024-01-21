package tgb.btc.rce.vo;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.FiatCurrency;

public class InlineCalculatorVO {

    private CryptoCurrency cryptoCurrency;
    private FiatCurrency fiatCurrency;
    private Boolean isSwitched;
    private String sum;
    private Boolean isOn;

    public Boolean getOn() {
        return isOn;
    }

    public void setOn(Boolean on) {
        isOn = on;
    }

    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public FiatCurrency getFiatCurrency() {
        return fiatCurrency;
    }

    public void setFiatCurrency(FiatCurrency fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }

    public Boolean getSwitched() {
        return isSwitched;
    }

    public void setSwitched(Boolean switched) {
        isSwitched = switched;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
