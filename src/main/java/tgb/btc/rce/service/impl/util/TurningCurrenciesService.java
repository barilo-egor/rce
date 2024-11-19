package tgb.btc.rce.service.impl.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.service.properties.CurrenciesTurningPropertiesReader;
import tgb.btc.rce.service.util.ITurningCurrenciesService;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TurningCurrenciesService implements ITurningCurrenciesService {

    public final Map<CryptoCurrency, Boolean> buyTurning = new EnumMap<>(CryptoCurrency.class);
    public final Map<CryptoCurrency, Boolean> sellTurning = new EnumMap<>(CryptoCurrency.class);

    private CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader;

    @Autowired
    public void setCurrenciesTurningPropertiesReader(CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader) {
        this.currenciesTurningPropertiesReader = currenciesTurningPropertiesReader;
    }

    @PostConstruct
    private void init() {
        for (CryptoCurrency currency : CryptoCurrency.values()) {
            buyTurning.put(currency, currenciesTurningPropertiesReader.getBoolean("buy." + currency.name()));
            sellTurning.put(currency, currenciesTurningPropertiesReader.getBoolean("sell." + currency.name()));
        }
    }

    @Override
    public boolean getIsOn(CryptoCurrency cryptoCurrency, DealType dealType) {
        if (DealType.BUY.equals(dealType)) return BooleanUtils.isTrue(buyTurning.get(cryptoCurrency));
        else return BooleanUtils.isTrue(sellTurning.get(cryptoCurrency));
    }

    @Override
    public List<CryptoCurrency> getSwitchedOnByDealType(DealType dealType) {
        return Arrays.stream(CryptoCurrency.values())
                .filter(cryptoCurrency -> DealType.BUY.equals(dealType)
                        ? buyTurning.get(cryptoCurrency)
                        : sellTurning.get(cryptoCurrency)).collect(Collectors.toList());
    }

    @Override
    public void addBuy(CryptoCurrency currency, boolean isOn) {
        buyTurning.put(currency, isOn);
    }

    @Override
    public void addSell(CryptoCurrency currency, boolean isOn) {
        sellTurning.put(currency, isOn);
    }
}
