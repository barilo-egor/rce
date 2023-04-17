package tgb.btc.rce.util;

import lombok.SneakyThrows;
import org.json.JSONObject;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
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

//    public static final String BTC_USD_URL_BLOCKCHAIN = "https://blockchain.info/ticker"; // last

    public static final String BTC_USD_URL_BLOCKCHAIN = "https://api.blockchain.com/v3/exchange/tickers/BTC-USD"; // last


    public static final int MAX_BTC_AMOUNT = 1;

    public static BigDecimal convertCryptoToRub(CryptoCurrency cryptoCurrency, Double sum, DealType dealType) {
        BigDecimal fix = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getFix(cryptoCurrency, dealType)));
        BigDecimal usdCourse = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.USD_COURSE));
        BigDecimal commission = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getCommission(cryptoCurrency, dealType)));
        BigDecimal fixCommission = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getFixCommission(cryptoCurrency, dealType)));
        BigDecimal transactionalCommission =
                BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getTransactionCommission(cryptoCurrency)));

        BigDecimal currency;
        switch (cryptoCurrency) {
            case BITCOIN:
                currency = getBtcCurrency();
                break;
            case LITECOIN:
                currency = getLtcCurrency();
                break;
            case USDT:
                currency = getUsdtCurrency();
                break;
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
        switch (dealType) {
            case BUY:
                return getAmount(BigDecimal.valueOf(sum), usdCourse, fix, commission, fixCommission, transactionalCommission,
                        currency);
            case SELL:
                return getAmountForSell(BigDecimal.valueOf(sum), usdCourse, fix, commission, fixCommission, currency);
            default:
                throw new BaseException("Не найден тип сделки для расчета суммы.");
        }
    }

    private static BigDecimal getAmountForSell(BigDecimal amount, BigDecimal course, BigDecimal fix,
                                        BigDecimal percentCommission, BigDecimal fixCommission,
                                        BigDecimal cryptoCurrency) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        BigDecimal commission = getCommissionForSell(rub, percentCommission);
        BigDecimal total = BigDecimalUtil.subtractHalfUp(rub, commission);
        return rub.compareTo(fix) < 0 ? BigDecimalUtil.subtractHalfUp(rub, fixCommission) : total;
    }

    public static BigDecimal getCommissionForSell(BigDecimal rub, BigDecimal percentCommission) {
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public static BigDecimal getCommissionForSell(BigDecimal amount, CryptoCurrency cryptoCurrency, DealType dealType) {
        BigDecimal currency;
        switch (cryptoCurrency) {
            case BITCOIN:
                currency = getBtcCurrency();
                break;
            case LITECOIN:
                currency = getLtcCurrency();
                break;
            case USDT:
                currency = getUsdtCurrency();
                break;
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, currency);
        BigDecimal course = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.USD_COURSE));
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        BigDecimal percentCommission = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getCommission(cryptoCurrency, dealType)));
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    private static BigDecimal getAmount(BigDecimal amount, BigDecimal course, BigDecimal fix,
                                        BigDecimal percentCommission, BigDecimal fixCommission,
                                        BigDecimal transactionCommission, BigDecimal cryptoCurrency) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        BigDecimal commission = getCommission(amount, cryptoCurrency, percentCommission, course);
        if (Objects.nonNull(transactionCommission)) rub = BigDecimalUtil.addHalfUp(rub,
                BigDecimalUtil.multiplyHalfUp(transactionCommission, course));
        BigDecimal total = BigDecimalUtil.addHalfUp(rub, commission);
        return rub.compareTo(fix) < 0 ? BigDecimalUtil.addHalfUp(rub, fixCommission) : total;
    }

    private static BigDecimal getCommission(BigDecimal amount, BigDecimal cryptoCurrency, BigDecimal percentCommission,
                                            BigDecimal course) {
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public static BigDecimal getCommission(BigDecimal amount, CryptoCurrency cryptoCurrency, DealType dealType) {
        BigDecimal fix = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getFix(cryptoCurrency, dealType)));
        BigDecimal currency;
        switch (cryptoCurrency) {
            case BITCOIN:
                currency = getBtcCurrency();
                break;
            case LITECOIN:
                currency = getLtcCurrency();
                break;
            case USDT:
                currency = getUsdtCurrency();
                break;
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
        BigDecimal percentCommission = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getCommission(cryptoCurrency, dealType)));
        BigDecimal course = BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.USD_COURSE));
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, currency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        if (rub.doubleValue() <= fix.doubleValue()) {
            return BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.getFixCommission(cryptoCurrency, dealType)));
        }
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public static BigDecimal getPercentsFactor(BigDecimal sum) {
        return BigDecimalUtil.divideHalfUp(sum, BigDecimal.valueOf(100));
    }

    @SneakyThrows
    public static BigDecimal getUsdtCurrency() {
        String currency = BotVariablePropertiesUtil.getVariable(BotVariableType.USDT_COURSE);
        return parse(currency, CryptoCurrency.USDT);
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
        Object obj = currency.get("last_trade_price");
        return parse(obj, CryptoCurrency.BITCOIN);
    }

    public static BigDecimal parse(Object obj, CryptoCurrency cryptoCurrency) {
        double sum;
        try {
            if (cryptoCurrency.getRateClass().equals(String.class)) {
                sum = Double.parseDouble((String) obj);
            } else if(cryptoCurrency.getRateClass().equals(Double.class)) {
                sum = (Double) obj;
            } else throw new BaseException("Не найден тип курса из апи.");
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
        } catch (Exception ex) {
            throw new BaseException("Проблема при получении курса. Создание заявки для этой валюты пока что невозможно.");
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
