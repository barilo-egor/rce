package tgb.btc.rce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.library.constants.enums.bot.FiatCurrency;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPrise {

    private int sum;

    private int minPrise;

    private int maxPrise;

    private FiatCurrency fiatCurrency;

    public ReviewPrise(String callbackQueryData) {
        String[] data = callbackQueryData.split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        this.minPrise = Integer.parseInt(data[1]);
        this.maxPrise = Integer.parseInt(data[2]);
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getMinPrise() {
        return minPrise;
    }

    public void setMinPrise(int minPrise) {
        this.minPrise = minPrise;
    }

    public int getMaxPrise() {
        return maxPrise;
    }

    public void setMaxPrise(int maxPrise) {
        this.maxPrise = maxPrise;
    }

    public FiatCurrency getFiatCurrency() {
        return fiatCurrency;
    }

    public void setFiatCurrency(FiatCurrency fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }

}
