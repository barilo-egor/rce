package tgb.btc.rce.util;

import lombok.SneakyThrows;
import org.json.JSONObject;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DeliveryType;
import tgb.btc.rce.exception.BaseException;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ConverterUtil {
    private ConverterUtil() {
    }

    // в комментарии проперти джсона с курсом
    public static final String BTC_USD_URL_BINANCE = "https://api1.binance.com/api/v3/avgPrice?symbol=BTCUSDT";

    public static final String LTC_USD_URL_BINANCE = "https://api1.binance.com/api/v3/avgPrice?symbol=LTCUSDT"; // price

    public static final String USDT_USD_RATE = "https://www.bitstamp.net/api/v2/ticker/usdtusd/"; // last

    public static final String BTC_USD_URL_BLOCKCHAIN = "https://blockchain.info/ticker"; // last


    public static final int MAX_BTC_AMOUNT = 1;

    public static BigDecimal convertCryptoToRub(CryptoCurrency cryptoCurrency, Double sum) {
        BigDecimal fix = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.FIX));
        BigDecimal usdCourse = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.USD_COURSE));
        BigDecimal commission = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.COMMISSION));
        BigDecimal fixCommission = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.FIX_COMMISSION));
        BigDecimal transactionalCommission =
                BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.TRANSACTION_COMMISSION));

        BigDecimal currency;
        switch (cryptoCurrency) {
            case BITCOIN:
                currency = getBtcCurrency();
                break;
            case LITECOIN:
                currency = getLtcCurrency();
                break;
            case TETHER:
                currency = getUsdtCurrency();
                break;
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
        return getAmount(BigDecimal.valueOf(sum), usdCourse, fix, commission, fixCommission, transactionalCommission,
                currency);
    }

    private static BigDecimal getAmount(BigDecimal amount, BigDecimal course, BigDecimal fix,
                                        BigDecimal percentCommission, BigDecimal fixCommission,
                                        BigDecimal transactionCommission, BigDecimal cryptoCurrency) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        if (Objects.nonNull(transactionCommission)) rub = BigDecimalUtil.addHalfUp(rub,
                BigDecimalUtil.multiplyHalfUp(transactionCommission, course));
        BigDecimal commission = BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
        BigDecimal total = BigDecimalUtil.addHalfUp(rub, commission);
        return rub.compareTo(fix) < 0 ? BigDecimalUtil.addHalfUp(rub, fixCommission) : total;
    }

    private static BigDecimal getPercentsFactor(BigDecimal percents) {
        return BigDecimalUtil.divideHalfUp(percents, BigDecimal.valueOf(100));
    }

    @SneakyThrows
    public static BigDecimal getUsdtCurrency() {
        JSONObject currency = readJsonFromUrl(ConverterUtil.USDT_USD_RATE);
        Object obj = currency.get("last");
        return parse(obj, CryptoCurrency.TETHER);
    }


    @SneakyThrows
    public static BigDecimal getLtcCurrency() {
        JSONObject currency = readJsonFromUrl(ConverterUtil.LTC_USD_URL_BINANCE);
        Object obj = currency.get("price");
        return parse(obj, CryptoCurrency.LITECOIN);
    }

    @SneakyThrows
    public static BigDecimal getBtcCurrency() {
        JSONObject currency = readJsonFromUrl(ConverterUtil.BTC_USD_URL_BLOCKCHAIN);
        Object obj = currency.getJSONObject("USD").get("last");
        return parse(obj, CryptoCurrency.BITCOIN);
    }

    public static BigDecimal parse(Object obj, CryptoCurrency cryptoCurrency) {
        double sum;
        try {
            sum = Double.parseDouble((String) obj);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            throw new BaseException("Ошибки при парсинге курса " + cryptoCurrency.getShortName() + ".");
        }
        return BigDecimal.valueOf(sum);
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
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
