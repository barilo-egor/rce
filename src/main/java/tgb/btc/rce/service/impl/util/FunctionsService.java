package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.service.properties.FunctionsPropertiesReader;
import tgb.btc.rce.service.IFunctionsService;

@Service
public class FunctionsService implements IFunctionsService {

    private FunctionsPropertiesReader functionsPropertiesReader;

    @Autowired
    public void setFunctionsPropertiesReader(FunctionsPropertiesReader functionsPropertiesReader) {
        this.functionsPropertiesReader = functionsPropertiesReader;
    }

    @Override
    public Boolean getSumToReceive(CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency) {
        return functionsPropertiesReader.getBoolean("sum.to.receive." + cryptoCurrency.getShortName(), false)
                && FiatCurrency.RUB.equals(fiatCurrency) ;
    }
}
