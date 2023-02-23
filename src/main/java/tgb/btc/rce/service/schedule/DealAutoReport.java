package tgb.btc.rce.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DealAutoReport {

    public DealRepository dealRepository;

    public UserRepository userRepository;

    public IResponseSender responseSender;

    public static LocalDate YESTERDAY;

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Async
    public void everyDay() {
        YESTERDAY = LocalDate.now().minusDays(1);

        BigDecimal amountOfSoldBtc = getSellCryptoAmount(CryptoCurrency.BITCOIN, 8);
        BigDecimal amountOfSoldLitecoin = getSellCryptoAmount(CryptoCurrency.LITECOIN, 5);
        BigDecimal amountOfSoldUsdt = getSellCryptoAmount(CryptoCurrency.USDT, 0);

        BigDecimal amountOfPurchasedBtc = getBuyCryptoAmount(CryptoCurrency.BITCOIN, 8);
        BigDecimal amountOfPurchasedLitecoin = getBuyCryptoAmount(CryptoCurrency.LITECOIN, 5);
        BigDecimal amountOfPurchasedUsdt = getSellCryptoAmount(CryptoCurrency.USDT, 0);

//        BigDecimal amountOfSoldRubForBtc = dealRepository.getAmountSum(true, DealType.SELL, yesterday, CryptoCurrency.BITCOIN)
//                .setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros();
//        BigDecimal amountOfSoldRubForLitecoin = dealRepository.getAmountSum(true, DealType.SELL, yesterday, CryptoCurrency.LITECOIN)
//                .setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros();
//        BigDecimal amountOfSoldRubForUsdt = dealRepository.getAmountSum(true, DealType.SELL, yesterday, CryptoCurrency.USDT)
//                .setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros();
//        BigDecimal totalSoldRubAmount = amountOfSoldRubForBtc.add(amountOfSoldRubForLitecoin).add(amountOfSoldRubForUsdt)
//                .setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros();

        BigDecimal amountOfPurchasedRubForBtc = getBuyAmount(CryptoCurrency.BITCOIN, 0);
        BigDecimal amountOfPurchasedRubForLitecoin = getBuyAmount(CryptoCurrency.LITECOIN, 0);
        BigDecimal amountOfPurchasedRubForUsdt = getBuyAmount(CryptoCurrency.USDT, 0);
        BigDecimal totalPurchasedRubAmount = amountOfPurchasedRubForBtc
                .add(amountOfPurchasedRubForLitecoin)
                .add(amountOfPurchasedRubForUsdt)
                .setScale(0, RoundingMode.HALF_DOWN)
                .stripTrailingZeros();


        Integer newUsersCount = userRepository.count(LocalDateTime.of(YESTERDAY, LocalTime.of(0, 0, 0)),
                LocalDateTime.of(YESTERDAY, LocalTime.of(23, 59, 59)));

//        Integer newPartners = userRepository.count();

        String report = "Продано BTC: " + amountOfSoldBtc + "\n"
                + "Продано Litecoin: " + amountOfSoldLitecoin + "\n"
                + "Продано USDT: " + amountOfSoldUsdt + "\n\n"
                + "Куплено BTC: " + amountOfPurchasedBtc + "\n"
                + "Куплено Litecoin: " + amountOfPurchasedLitecoin + "\n"
                + "Куплено USDT: " + amountOfPurchasedUsdt + "\n\n"
                + "Получено рублей от BTC: " + amountOfPurchasedRubForBtc + "\n"
                + "Получено рублей от Litecoin: " + amountOfPurchasedRubForLitecoin + "\n"
                + "Получено рублей от USDT: " + amountOfPurchasedRubForUsdt + "\n"
                + "Всего получено рублей: " + totalPurchasedRubAmount;
        responseSender.sendMessage(393928596L, "123");
    }

    private BigDecimal getBuyCryptoAmount(CryptoCurrency cryptoCurrency, int scale) {
        return getCryptoAmount(DealType.BUY, cryptoCurrency, scale);
    }

    private BigDecimal getSellCryptoAmount(CryptoCurrency cryptoCurrency, int scale) {
        return getCryptoAmount(DealType.SELL, cryptoCurrency, scale);
    }

    private BigDecimal getCryptoAmount(DealType dealType, CryptoCurrency cryptoCurrency, int scale) {
        return dealRepository.getCryptoAmountSum(true, dealType, YESTERDAY, cryptoCurrency)
                .setScale(scale, RoundingMode.HALF_DOWN).stripTrailingZeros();
    }

    private BigDecimal getBuyAmount(CryptoCurrency cryptoCurrency, int scale) {
        return dealRepository.getAmountSum(true, DealType.BUY, YESTERDAY, cryptoCurrency)
                .setScale(scale, RoundingMode.HALF_DOWN).stripTrailingZeros();
    }

    @Scheduled(cron = "0 5 0 * * MON")
    @Async
    public void everyWeek() {

    }
}
