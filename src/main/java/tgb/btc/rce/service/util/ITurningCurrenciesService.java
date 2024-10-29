package tgb.btc.rce.service.util;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;

import java.util.List;

public interface ITurningCurrenciesService {
    boolean getIsOn(CryptoCurrency cryptoCurrency, DealType dealType);

    List<CryptoCurrency> getSwitchedOnByDealType(DealType dealType);

    void addBuy(CryptoCurrency currency, boolean isOn);

    void addSell(CryptoCurrency currency, boolean isOn);
}
