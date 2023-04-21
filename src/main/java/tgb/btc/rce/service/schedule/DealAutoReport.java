package tgb.btc.rce.service.schedule;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.vo.DealReportData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

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

        sendReport(DealReportData.builder()
                           .period("день")
                           .firstDay(YESTERDAY)
                           .lastDay(YESTERDAY)
                           .build());
    }

    @Scheduled(cron = "0 5 0 * * MON")
    @Async
    public void everyWeek() {
        LocalDate firstDay = LocalDate.now().minusDays(7);
        LocalDate lastDay = LocalDate.now().minusDays(1);

        sendReport(DealReportData.builder()
                           .period("неделю")
                           .firstDay(firstDay)
                           .lastDay(lastDay)
                           .build());
    }

    @Scheduled(cron = "0 10 0 1 * *")
    @Async
    public void everyMonth() {
        LocalDate firstDay = LocalDate.now().minusDays(1).withDayOfMonth(1);
        LocalDate lastDay = LocalDate.now().minusDays(1).withDayOfMonth(firstDay.getMonth().length(firstDay.isLeapYear()));
        sendReport(DealReportData.builder()
                           .period("месяц")
                           .firstDay(firstDay)
                           .lastDay(lastDay)
                           .build());
    }

    private void sendReport(DealReportData data) {
        try {
            LocalDateTime dateTimeBegin = LocalDateTime.of(data.getFirstDay(), LocalTime.of(0, 0, 0));
            LocalDateTime dateTimeEnd = LocalDateTime.of(data.getLastDay(), LocalTime.of(23, 59, 59));

            Integer newUsersCount = userRepository.countByRegistrationDate(dateTimeBegin, dateTimeEnd);
            List<Long> allNewPartnersChatIds = userRepository.getChatIdsByRegistrationDateAndFromChatIdNotNull(
                    dateTimeBegin, dateTimeEnd);
            int allNewPartnersCount = (Objects.nonNull(allNewPartnersChatIds)
                                       ? allNewPartnersChatIds.size()
                                       : 0);

            int newActivePartnersCount = 0;

            for (Long chatId : allNewPartnersChatIds) {
                if (dealRepository.getCountPassedByChatId(chatId) > 0) newActivePartnersCount++;
            }

            BigDecimal amountOfPurchasedRubForBtc = getBuyAmount(CryptoCurrency.BITCOIN, data.getFirstDay(),
                                                                 data.getLastDay());
            BigDecimal amountOfPurchasedRubForLitecoin = getBuyAmount(CryptoCurrency.LITECOIN, data.getFirstDay(),
                                                                      data.getLastDay());
            BigDecimal amountOfPurchasedRubForUsdt = getBuyAmount(CryptoCurrency.USDT, data.getFirstDay(),
                                                                  data.getLastDay());
            BigDecimal totalPurchasedRubAmount = amountOfPurchasedRubForBtc
                    .add(amountOfPurchasedRubForLitecoin)
                    .add(amountOfPurchasedRubForUsdt)
                    .setScale(0, RoundingMode.HALF_DOWN)
                    .stripTrailingZeros();

            String report = "Отчет за " + data.getPeriod() + ":"
                    + "Куплено BTC: " + getSellCryptoAmount(CryptoCurrency.BITCOIN, 8) + "\n"
                    + "Куплено Litecoin: " + getSellCryptoAmount(CryptoCurrency.LITECOIN, 5) + "\n"
                    + "Куплено USDT: " + getSellCryptoAmount(CryptoCurrency.USDT, 0) + "\n\n"
                    + "Получено рублей от BTC: " + amountOfPurchasedRubForBtc + "\n"
                    + "Получено рублей от Litecoin: " + amountOfPurchasedRubForLitecoin + "\n"
                    + "Получено рублей от USDT: " + amountOfPurchasedRubForUsdt + "\n"
                    + "Продано BTC: " + getBuyCryptoAmount(CryptoCurrency.BITCOIN, 8) + "\n"
                    + "Продано Litecoin: " + getBuyCryptoAmount(CryptoCurrency.LITECOIN, 5) + "\n"
                    + "Продано USDT: " + getSellCryptoAmount(CryptoCurrency.USDT, 0) + "\n\n"
                    + "Всего получено рублей: " + totalPurchasedRubAmount
                    + "\n"
                    + "Количество новых пользователей: " + newUsersCount
                    + "Количество новых партнеров: " + allNewPartnersCount
                    + "Количество активных новых партнеров: " + newActivePartnersCount;
            userRepository.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, report));
        } catch (Exception e) {
            String message = "Ошибка при формировании периодического отчета за " + data.getPeriod()  + ":\n"
                    + e.getMessage() + "\n"
                    + ExceptionUtils.getFullStackTrace(e);
            userRepository.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message));
        }
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

    private BigDecimal getBuyAmount(CryptoCurrency cryptoCurrency, LocalDate dateFrom, LocalDate dateTo) {
        return getAmount(cryptoCurrency, dateFrom, dateTo, DealType.BUY);
    }

    private BigDecimal getAmount(CryptoCurrency cryptoCurrency, LocalDate dateFrom, LocalDate dateTo, DealType dealType) {
        BigDecimal totalAmount;
        if (Objects.nonNull(dateTo)) {
            totalAmount = dealRepository.getTotalAmountSum(true, dealType, dateFrom, dateTo, cryptoCurrency);
        } else {
            totalAmount = dealRepository.getTotalAmountSum(true, dealType, dateFrom, cryptoCurrency);
        }
        return Objects.nonNull(totalAmount)
               ? totalAmount.setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros()
               : BigDecimal.ZERO;
    }
}
