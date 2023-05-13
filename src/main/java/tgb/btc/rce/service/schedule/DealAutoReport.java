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

            if (dealRepository.getCountByPeriod(dateTimeBegin, dateTimeEnd) == 0) {
                userRepository.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId,
                        "Нет сделок за " + data.getPeriod() + "."));
                return;
            }
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

            BigDecimal amountOfPurchasedRubForBtc = getBuyAmount(CryptoCurrency.BITCOIN, dateTimeBegin, dateTimeEnd);
            BigDecimal amountOfPurchasedRubForLitecoin = getBuyAmount(CryptoCurrency.LITECOIN, dateTimeBegin, dateTimeEnd);
            BigDecimal amountOfPurchasedRubForUsdt = getBuyAmount(CryptoCurrency.USDT, dateTimeBegin, dateTimeEnd);
            BigDecimal amountOfPurchasedRubForXmr = getBuyAmount(CryptoCurrency.MONERO, dateTimeBegin, dateTimeEnd);
            BigDecimal totalPurchasedRubAmount = amountOfPurchasedRubForBtc
                    .add(amountOfPurchasedRubForLitecoin)
                    .add(amountOfPurchasedRubForUsdt)
                    .add(amountOfPurchasedRubForXmr)
                    .setScale(0, RoundingMode.HALF_DOWN)
                    .stripTrailingZeros();

            String report = "Отчет за " + data.getPeriod() + ":\n"
                    + "Куплено BTC: " + getBuyCryptoAmount(CryptoCurrency.BITCOIN, 8, dateTimeBegin, dateTimeEnd).toPlainString() + "\n"
                    + "Куплено Litecoin: " + getBuyCryptoAmount(CryptoCurrency.LITECOIN, 5, dateTimeBegin, dateTimeEnd).toPlainString() + "\n"
                    + "Куплено USDT: " + getBuyCryptoAmount(CryptoCurrency.USDT, 0, dateTimeBegin, dateTimeEnd).toPlainString() + "\n"
                    + "Куплено XMR: " + getBuyCryptoAmount(CryptoCurrency.MONERO, 0, dateTimeBegin, dateTimeEnd).toPlainString() + "\n\n"
                    + "Получено рублей от BTC: " + amountOfPurchasedRubForBtc.toPlainString() + "\n"
                    + "Получено рублей от Litecoin: " + amountOfPurchasedRubForLitecoin.toPlainString() + "\n"
                    + "Получено рублей от USDT: " + amountOfPurchasedRubForUsdt.toPlainString() + "\n"
                    + "Получено рублей от XMR: " + amountOfPurchasedRubForXmr.toPlainString() + "\n\n"
                    + "Продано BTC: " + getSellCryptoAmount(CryptoCurrency.BITCOIN, 8, dateTimeBegin, dateTimeEnd).toPlainString() + "\n"
                    + "Продано Litecoin: " + getSellCryptoAmount(CryptoCurrency.LITECOIN, 5, dateTimeBegin, dateTimeEnd).toPlainString() + "\n"
                    + "Продано USDT: " + getSellCryptoAmount(CryptoCurrency.USDT, 0, dateTimeBegin, dateTimeEnd).toPlainString() + "\n"
                    + "Продано XMR: " + getSellCryptoAmount(CryptoCurrency.MONERO, 0, dateTimeBegin, dateTimeEnd).toPlainString() + "\n\n"
                    + "Всего получено рублей: " + totalPurchasedRubAmount.toPlainString()
                    + "\n"
                    + "Количество новых пользователей: " + newUsersCount + "\n"
                    + "Количество новых партнеров: " + allNewPartnersCount + "\n"
                    + "Количество активных новых партнеров: " + newActivePartnersCount;
            userRepository.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, report));
        } catch (Exception e) {
            String message = "Ошибка при формировании периодического отчета за " + data.getPeriod() + ":\n"
                    + e.getMessage() + "\n"
                    + ExceptionUtils.getFullStackTrace(e);
            userRepository.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message));
        }
    }

    private BigDecimal getBuyCryptoAmount(CryptoCurrency cryptoCurrency, int scale, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return getCryptoAmount(DealType.BUY, cryptoCurrency, scale, dateFrom, dateTo);
    }

    private BigDecimal getSellCryptoAmount(CryptoCurrency cryptoCurrency, int scale, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return getCryptoAmount(DealType.SELL, cryptoCurrency, scale, dateFrom, dateTo);
    }

    private BigDecimal getCryptoAmount(DealType dealType, CryptoCurrency cryptoCurrency, int scale, LocalDateTime dateFrom, LocalDateTime dateTo) {
        // TODO поправить scale для usdt, выводит 2.6E+2 место 260
        BigDecimal totalCryptoAmount = dealRepository.getCryptoAmountSum(true, dealType, dateFrom, dateTo, cryptoCurrency);
        return Objects.nonNull(totalCryptoAmount)
                ? totalCryptoAmount.setScale(scale, RoundingMode.HALF_DOWN).stripTrailingZeros()
                : BigDecimal.ZERO;
    }

    private BigDecimal getBuyAmount(CryptoCurrency cryptoCurrency, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return getAmount(cryptoCurrency, dateFrom, dateTo, DealType.BUY);
    }

    private BigDecimal getAmount(CryptoCurrency cryptoCurrency, LocalDateTime dateFrom, LocalDateTime dateTo, DealType dealType) {
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
