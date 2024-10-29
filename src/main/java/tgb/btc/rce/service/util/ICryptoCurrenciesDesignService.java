package tgb.btc.rce.service.util;

import tgb.btc.library.constants.enums.bot.CryptoCurrency;

public interface ICryptoCurrenciesDesignService {

    String getDisplayName(CryptoCurrency currency);

    CryptoCurrency fromDisplayName(String displayName);
}
