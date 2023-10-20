package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.exception.ReadFromUrlException;
import tgb.btc.rce.enums.CryptoApi;
import tgb.btc.rce.enums.ManualBTCApi;
import tgb.btc.rce.enums.properties.BotProperties;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
public class CryptoCurrencyService {

    private static CryptoApi CURRENT_BTC_USD_API;

    private static final boolean IS_BTC_USD_API_MANUAL;

    static {
        String propertyApi = BotProperties.BOT_CONFIG.getString("bot.btc.api");
        if (Objects.isNull(propertyApi)) {
            CURRENT_BTC_USD_API = getAvailable();
            IS_BTC_USD_API_MANUAL = false;
        } else {
            ManualBTCApi manualBTCApi = ManualBTCApi.valueOf(propertyApi);
            if (ManualBTCApi.BINANCE.equals(manualBTCApi)) CURRENT_BTC_USD_API = CryptoApi.BTC_USD_BINANCE;
            else CURRENT_BTC_USD_API = CryptoApi.BTC_USD_BLOCKCHAIN;
            IS_BTC_USD_API_MANUAL = true;
        }
    }

    private static CryptoApi getAvailable() {
        for (CryptoApi cryptoApi : CryptoApi.BTC_USD) {
            try {
                cryptoApi.getCourse();
                CURRENT_BTC_USD_API = cryptoApi;
                return cryptoApi;
            } catch (ReadFromUrlException ignored) {
            }
        }
        throw new BaseException("Не получилось связаться ни с одним API для BTC.");
    }

    public BigDecimal getCurrency(CryptoCurrency cryptoCurrency) {
        switch (cryptoCurrency) {
            case BITCOIN:
                if (IS_BTC_USD_API_MANUAL) return CURRENT_BTC_USD_API.getCourse();
                else {
                    try {
                        return CURRENT_BTC_USD_API.getCourse();
                    } catch (ReadFromUrlException e) {
                        synchronized (this) {
                            CURRENT_BTC_USD_API = getAvailable();
                        }
                        return CURRENT_BTC_USD_API.getCourse();
                    }
                }
            case LITECOIN:
                return CryptoApi.LTC_USD_BINANCE.getCourse();
            case USDT:
                return BigDecimal.valueOf(Double.parseDouble(BotVariablePropertiesUtil.getVariable(VariableType.USDT_COURSE)));
            case MONERO:
                return CryptoApi.XMR_USD_COINREMITTER.getCourse();
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
    }

    public BigDecimal getCurrencyToFiat(FiatCurrency fiatCurrency, CryptoCurrency cryptoCurrency) {
        if (!FiatCurrency.RUB.equals(fiatCurrency))
            throw new BaseException("Реализация предусмотрена только для " + FiatCurrency.RUB.name());
        switch (cryptoCurrency) {
            case BITCOIN:
                return CryptoApi.BTC_RUB_BINANCE.getCourse();
            case LITECOIN:
                return CryptoApi.LTC_RUB_BINANCE.getCourse();
            default:
                throw new BaseException("Для данной криптовалюты не предусмотрена реализация.");
        }
    }
}
