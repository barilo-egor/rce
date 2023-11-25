package tgb.btc.rce.enums;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.ReadFromUrlException;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

@Slf4j
public enum CryptoApi {
    BTC_USD_BLOCKCHAIN("https://api.blockchain.com/v3/exchange/tickers/BTC-USD",
            jsonObject -> jsonObject.get("last_trade_price")),
    BTC_USD_BINANCE("https://api1.binance.com/api/v3/avgPrice?symbol=BTCUSDT",
            jsonObject -> jsonObject.get("price")),
    LTC_USD_BINANCE("https://api1.binance.com/api/v3/avgPrice?symbol=LTCUSDT",
            jsonObject -> jsonObject.get("price")),
    BTC_RUB_BINANCE("https://api1.binance.com/api/v3/avgPrice?symbol=BTCRUB",
            jsonObject -> jsonObject.get("price")),
    LTC_RUB_BINANCE("https://api1.binance.com/api/v3/avgPrice?symbol=LTCRUB",
            jsonObject -> jsonObject.get("price")),
//    XMR_USD_COINREMITTER("https://coinremitter.com/api/v3/get-coin-rate",
//            jsonObject -> ((JSONObject) ((JSONObject) jsonObject.get("data")).get("XMR")).get("price"))
    XMR_USD_COINREMITTER("https://min-api.cryptocompare.com/data/price?fsym=XMR&tsyms=BTC,USD,EUR",
            jsonObject -> jsonObject.get("USD"))
    ;

    final String url;

    final Function<JSONObject, Object> currencyReceiver;

    CryptoApi(String url, Function<JSONObject, Object> currencyReceiver) {
        this.url = url;
        this.currencyReceiver = currencyReceiver;
    }

    public static final List<CryptoApi> BTC_USD = List.of(CryptoApi.BTC_USD_BINANCE, CryptoApi.BTC_USD_BLOCKCHAIN);

    public BigDecimal getCourse() {
        return parse(this.currencyReceiver.apply(readJsonFromUrl(this.url)));
    }

    private static BigDecimal parse(Object price) {
        double parsedPrice;
        if (price instanceof String) parsedPrice = Double.parseDouble((String) price);
        else if (price instanceof Double) parsedPrice = (Double) price;
        else if (price instanceof Integer) parsedPrice = ((Integer) price).doubleValue();
        else throw new BaseException("Не определен тип значения из JSON.");
        return BigDecimal.valueOf(parsedPrice);
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (Exception ex) {
            log.error("Ошика при получении курса по url=" + url, ex);
            throw new ReadFromUrlException("Проблема при получении курса. Создание заявки для этой валюты пока что невозможно.");
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
