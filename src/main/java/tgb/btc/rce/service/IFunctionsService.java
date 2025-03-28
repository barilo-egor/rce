package tgb.btc.rce.service;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.FiatCurrency;

public interface IFunctionsService {
    Boolean getSumToReceive(CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency);
}
