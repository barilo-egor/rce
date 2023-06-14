package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.ReadFromUrlException;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
public class CryptoCurrencyService {

    private static CryptoApi CURRENT_BTC_USD_API;

    static {
        String propertyApi = BotProperties.BOT_CONFIG.getString("bot.btc.api");
        if (Objects.isNull(propertyApi)) {
            CURRENT_BTC_USD_API = getAvailable();
        } else {
            ManualBTCApi manualBTCApi = ManualBTCApi.valueOf(propertyApi);
            if (ManualBTCApi.BINANCE.equals(manualBTCApi)) CURRENT_BTC_USD_API = CryptoApi.BTC_USD_BINANCE;
            else CURRENT_BTC_USD_API = CryptoApi.BTC_USD_BLOCKCHAIN;
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
                try {
                    return CURRENT_BTC_USD_API.getCourse();
                } catch (ReadFromUrlException e) {
                    CURRENT_BTC_USD_API = getAvailable();
                    return CURRENT_BTC_USD_API.getCourse();
                }
            case LITECOIN:
                return CryptoApi.LTC_USD_BINANCE.getCourse();
            case USDT:
                return BigDecimal.valueOf(Double.parseDouble(BotVariablePropertiesUtil.getVariable(BotVariableType.USDT_COURSE)));
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
