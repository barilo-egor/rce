package tgb.btc.rce.util;

import lombok.SneakyThrows;
import org.json.JSONObject;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class CalculateUtil {
    private CalculateUtil() {
    }

    // в комментарии проперти джсона с курсом
    public static final String BTC_USD_URL_BINANCE = "https://api1.binance.com/api/v3/avgPrice?symbol=BTCUSDT";

    public static final String LTC_USD_URL_BINANCE = "https://api1.binance.com/api/v3/avgPrice?symbol=LTCUSDT"; // price

    public static final String USDT_USD_RATE = "https://www.bitstamp.net/api/v2/ticker/usdtusd/"; // last

//    public static final String BTC_USD_URL_BLOCKCHAIN = "https://blockchain.info/ticker"; // last

    public static final String BTC_USD_URL_BLOCKCHAIN = "https://api.blockchain.com/v3/exchange/tickers/BTC-USD"; // last

    public static final String MONERO_URL_COINREMITTER = "https://coinremitter.com/api/v3/get-coin-rate";


    public static final int MAX_BTC_AMOUNT = 1;

    public static BigDecimal convertCryptoToRub(CryptoCurrency cryptoCurrency, Double sum, FiatCurrency fiatCurrency,
                                                DealType dealType) {
        BigDecimal fix = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal usdCourse = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal commission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal fixCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX_COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal transactionalCommission = BotVariablePropertiesUtil.getTransactionCommission(cryptoCurrency);

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
            case MONERO:
                currency = getXmrCurrency();
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

    public static BigDecimal getCommissionForSell(BigDecimal amount, CryptoCurrency cryptoCurrency,
                                                  FiatCurrency fiatCurrency, DealType dealType) {
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
            case MONERO:
                currency = getXmrCurrency();
                break;
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, currency);
        BigDecimal course = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        BigDecimal percentCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency,
                dealType, cryptoCurrency);
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

    public static BigDecimal getCommission(BigDecimal amount, CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency,
                                           DealType dealType) {
        BigDecimal fix = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX, fiatCurrency, dealType, cryptoCurrency);
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
            case MONERO:
                currency = getXmrCurrency();
                break;
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
        BigDecimal percentCommission = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal course = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrency, dealType, cryptoCurrency);
        BigDecimal usd = BigDecimalUtil.multiplyHalfUp(amount, currency);
        BigDecimal rub = BigDecimalUtil.multiplyHalfUp(usd, course);
        if (rub.doubleValue() <= fix.doubleValue()) {
            return BotVariablePropertiesUtil.getBigDecimal(BotVariableType.FIX_COMMISSION, fiatCurrency, dealType, cryptoCurrency);
        }
        return BigDecimalUtil.multiplyHalfUp(rub, getPercentsFactor(percentCommission));
    }

    public static BigDecimal getPercentsFactor(BigDecimal sum) {
        return BigDecimalUtil.divideHalfUp(sum, BigDecimal.valueOf(100));
    }

    @SneakyThrows
    public static BigDecimal getUsdtCurrency() {
        return BigDecimal.valueOf(Double.parseDouble(BotVariablePropertiesUtil.getVariable(BotVariableType.USDT_COURSE)));
    }


    @SneakyThrows
    public static BigDecimal getLtcCurrency() {
        JSONObject currency = readJsonFromUrl(CalculateUtil.LTC_USD_URL_BINANCE);
        Object obj = currency.get("price");
        return parse(obj, CryptoCurrency.LITECOIN);
    }

    @SneakyThrows
    public static BigDecimal getBtcCurrency() {
        Object obj;
        JSONObject currency;
        if (CurrencyApi.BINANCE.equals(CurrencyApi.valueOf(BotProperties.BOT_CONFIG_PROPERTIES.getString("bot.btc.api")))) {
            currency = readJsonFromUrl(CalculateUtil.BTC_USD_URL_BINANCE);
            obj = currency.get("price");
            return parse(obj, CryptoCurrency.BITCOIN, String.class);
        } else {
            currency = readJsonFromUrl(CalculateUtil.BTC_USD_URL_BLOCKCHAIN);
            obj = currency.get("last_trade_price");
            return parse(obj, CryptoCurrency.BITCOIN, Double.class);
        }
    }

    @SneakyThrows
    public static BigDecimal getXmrCurrency() {
        JSONObject currency = readJsonFromUrl(MONERO_URL_COINREMITTER);
        Object obj = ((JSONObject) ((JSONObject) readJsonFromUrl(MONERO_URL_COINREMITTER).get("data")).get("XMR")).get("price");
        return parse(obj, CryptoCurrency.MONERO);
    }

    public static BigDecimal parse(Object obj, CryptoCurrency cryptoCurrency) {
        return parse(obj, cryptoCurrency, cryptoCurrency.getRateClass());
    }

    public static BigDecimal parse(Object obj, CryptoCurrency cryptoCurrency, Class clazz) {
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

    public static BigDecimal calculateDiscount(DealType dealType, BigDecimal amount, BigDecimal discount) {
        BigDecimal totalDiscount = CalculateUtil.getPercentsFactor(amount).multiply(discount);
        return DealType.BUY.equals(dealType)
                ? amount.add(totalDiscount)
                : amount.subtract(totalDiscount);
    }
}
