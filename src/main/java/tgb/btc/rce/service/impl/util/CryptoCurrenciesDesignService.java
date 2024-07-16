package tgb.btc.rce.service.impl.util;

import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.properties.CryptoCurrencyDesignPropertiesReader;
import tgb.btc.rce.service.util.ICryptoCurrenciesDesignService;

@Service
public class CryptoCurrenciesDesignService implements ICryptoCurrenciesDesignService {

    private CryptoCurrencyDesignPropertiesReader cryptoCurrencyDesignPropertiesReader;

    @Override
    public String getDisplayName(CryptoCurrency currency) {
        return cryptoCurrencyDesignPropertiesReader.getString(currency.name());
    }

    @Override
    public CryptoCurrency fromDisplayName(String displayName) {
        return CryptoCurrency.valueOf(cryptoCurrencyDesignPropertiesReader.getKeys().stream()
                .filter(key -> displayName.equals(cryptoCurrencyDesignPropertiesReader.getString(key)))
                .findFirst()
                .orElseThrow(() -> new BaseException("Криптовалюта по значению " + displayName + " не найдена")));
    }
}
