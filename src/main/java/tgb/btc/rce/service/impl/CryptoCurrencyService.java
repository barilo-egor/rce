package tgb.btc.rce.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.CurrencyApi;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.util.BotVariablePropertiesUtil;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class CryptoCurrencyService {

    // в комментарии проперти джсона с курсом
    public static final String BTC_USD_URL_BINANCE = "https://api1.binance.com/api/v3/avgPrice?symbol=BTCUSDT";

    public static final String LTC_USD_URL_BINANCE = "https://api1.binance.com/api/v3/avgPrice?symbol=LTCUSDT"; // price

    public static final String USDT_USD_RATE = "https://www.bitstamp.net/api/v2/ticker/usdtusd/"; // last

//    public static final String BTC_USD_URL_BLOCKCHAIN = "https://blockchain.info/ticker"; // last

    public static final String BTC_USD_URL_BLOCKCHAIN = "https://api.blockchain.com/v3/exchange/tickers/BTC-USD"; // last

    public static final String MONERO_URL_COINREMITTER = "https://coinremitter.com/api/v3/get-coin-rate";

    public BigDecimal getCurrency(CryptoCurrency cryptoCurrency) {
        switch (cryptoCurrency) {
            case BITCOIN:
                return getBtcCurrency();
            case LITECOIN:
                return getLtcCurrency();
            case USDT:
                return getUsdtCurrency();
            case MONERO:
                return getXmrCurrency();
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
    }

    @SneakyThrows
    private BigDecimal getUsdtCurrency() {
        return BigDecimal.valueOf(Double.parseDouble(BotVariablePropertiesUtil.getVariable(BotVariableType.USDT_COURSE)));
    }


    @SneakyThrows
    private BigDecimal getLtcCurrency() {
        JSONObject currency = readJsonFromUrl(LTC_USD_URL_BINANCE);
        Object obj = currency.get("price");
        return parse(obj, CryptoCurrency.LITECOIN);
    }

    @SneakyThrows
    private BigDecimal getBtcCurrency() {
        Object obj;
        JSONObject currency;
        if (CurrencyApi.BINANCE.equals(CurrencyApi.valueOf(BotProperties.BOT_CONFIG_PROPERTIES.getString("bot.btc.api")))) {
            currency = readJsonFromUrl(BTC_USD_URL_BINANCE);
            obj = currency.get("price");
            return parse(obj, CryptoCurrency.BITCOIN, String.class);
        } else {
            currency = readJsonFromUrl(BTC_USD_URL_BLOCKCHAIN);
            obj = currency.get("last_trade_price");
            return parse(obj, CryptoCurrency.BITCOIN, Double.class);
        }
    }

    @SneakyThrows
    private BigDecimal getXmrCurrency() {
        JSONObject currency = readJsonFromUrl(MONERO_URL_COINREMITTER);
        Object obj = ((JSONObject) ((JSONObject) readJsonFromUrl(MONERO_URL_COINREMITTER).get("data")).get("XMR")).get("price");
        return parse(obj, CryptoCurrency.MONERO);
    }

    private BigDecimal parse(Object obj, CryptoCurrency cryptoCurrency) {
        return parse(obj, cryptoCurrency, cryptoCurrency.getRateClass());
    }

    private BigDecimal parse(Object obj, CryptoCurrency cryptoCurrency, Class clazz) {
        double sum;
        try {
            if (clazz.equals(String.class)) {
                sum = Double.parseDouble((String) obj);
            } else if(clazz.equals(Double.class)) {
                sum = (Double) obj;
            } else throw new BaseException("Не найден тип курса из апи.");
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            throw new BaseException("Ошибки при парсинге курса " + cryptoCurrency.getShortName() + ".");
        }
        return BigDecimal.valueOf(sum);
    }

    private JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (Exception ex) {
            log.error("Ошика при получении курса по url=" + url, ex);
            throw new BaseException("Проблема при получении курса. Создание заявки для этой валюты пока что невозможно.");
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
