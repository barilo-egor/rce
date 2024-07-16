package tgb.btc.rce.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.service.ITurningCurrenciesService;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TurningCurrenciesService implements ITurningCurrenciesService {

    public final Map<CryptoCurrency, Boolean> buyTurning = new EnumMap<>(CryptoCurrency.class);
    public final Map<CryptoCurrency, Boolean> sellTurning = new EnumMap<>(CryptoCurrency.class);

    @PostConstruct
    private void init() {
        for (CryptoCurrency currency : CryptoCurrency.values()) {
            buyTurning.put(currency, PropertiesPath.CURRENCIES_TURNING_PROPERTIES.getBoolean("buy." + currency.name()));
            sellTurning.put(currency, PropertiesPath.CURRENCIES_TURNING_PROPERTIES.getBoolean("sell." + currency.name()));
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
