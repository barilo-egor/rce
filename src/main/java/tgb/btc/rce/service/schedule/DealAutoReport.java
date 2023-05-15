package tgb.btc.rce.service.schedule;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.util.FiatCurrenciesUtil;
import tgb.btc.rce.vo.DealReportData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Отчет за ").append(data.getPeriod()).append(":\n").append("Куплено BTC: ")
                    .append(getBuyCryptoAmount(CryptoCurrency.BITCOIN, 8, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n").append("Куплено Litecoin: ")
                    .append(getBuyCryptoAmount(CryptoCurrency.LITECOIN, 5, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n").append("Куплено USDT: ")
                    .append(getBuyCryptoAmount(CryptoCurrency.USDT, 0, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n").append("Куплено XMR: ")
                    .append(getBuyCryptoAmount(CryptoCurrency.MONERO, 0, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n\n");
            Map<FiatCurrency, BigDecimal> totalAmounts = new HashMap<>();
            for (FiatCurrency fiatCurrency : FiatCurrenciesUtil.getFiatCurrencies()) {
                BigDecimal buyAmountBTC = getBuyAmount(CryptoCurrency.BITCOIN, dateTimeBegin, dateTimeEnd, fiatCurrency);
                stringBuilder.append("Получено ").append(fiatCurrency.getCode()).append(" от BTC: ").append(buyAmountBTC).append("\n");
                BigDecimal buyAmountLTC = getBuyAmount(CryptoCurrency.LITECOIN, dateTimeBegin, dateTimeEnd, fiatCurrency);
                stringBuilder.append("Получено ").append(fiatCurrency.getCode()).append(" от Litecoin: ").append(getBuyAmount(CryptoCurrency.LITECOIN, dateTimeBegin, dateTimeEnd, fiatCurrency)).append("\n");
                BigDecimal buyAmountUSDT = getBuyAmount(CryptoCurrency.USDT, dateTimeBegin, dateTimeEnd, fiatCurrency);
                stringBuilder.append("Получено ").append(fiatCurrency.getCode()).append(" от USDT: ").append(getBuyAmount(CryptoCurrency.USDT, dateTimeBegin, dateTimeEnd, fiatCurrency)).append("\n");
                BigDecimal buyAmountXMR = getBuyAmount(CryptoCurrency.MONERO, dateTimeBegin, dateTimeEnd, fiatCurrency);
                stringBuilder.append("Получено ").append(fiatCurrency.getCode()).append(" от XMR: ").append(getBuyAmount(CryptoCurrency.MONERO, dateTimeBegin, dateTimeEnd, fiatCurrency)).append("\n\n");
                totalAmounts.put(fiatCurrency, buyAmountBTC.add(buyAmountLTC).add(buyAmountUSDT).add(buyAmountXMR));
            }
            stringBuilder.append("Продано BTC: ")
                    .append(getSellCryptoAmount(CryptoCurrency.BITCOIN, 8, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n").append("Продано Litecoin: ")
                    .append(getSellCryptoAmount(CryptoCurrency.LITECOIN, 5, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n").append("Продано USDT: ")
                    .append(getSellCryptoAmount(CryptoCurrency.USDT, 0, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n").append("Продано XMR: ")
                    .append(getSellCryptoAmount(CryptoCurrency.MONERO, 0, dateTimeBegin, dateTimeEnd).toPlainString())
                    .append("\n\n");
            for (FiatCurrency fiatCurrency : FiatCurrenciesUtil.getFiatCurrencies()) {
                stringBuilder.append("Всего получено рублей от ").append(fiatCurrency.getCode()).append(" : ")
                        .append(totalAmounts.get(fiatCurrency).toPlainString()).append("\n");
            }
            stringBuilder.append("\n" + "Количество новых пользователей: ").append(newUsersCount).append("\n")
                    .append("Количество новых партнеров: ").append(allNewPartnersCount).append("\n")
                    .append("Количество активных новых партнеров: ").append(newActivePartnersCount);
            userRepository.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, stringBuilder.toString()));
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

    private BigDecimal getBuyAmount(CryptoCurrency cryptoCurrency, LocalDateTime dateFrom, LocalDateTime dateTo, FiatCurrency fiatCurrency) {
        return getAmount(cryptoCurrency, dateFrom, dateTo, DealType.BUY, fiatCurrency);
    }

    private BigDecimal getAmount(CryptoCurrency cryptoCurrency, LocalDateTime dateFrom, LocalDateTime dateTo, DealType dealType, FiatCurrency fiatCurrency) {
        BigDecimal totalAmount;
        if (Objects.nonNull(dateTo)) {
            totalAmount = dealRepository.getTotalAmountSum(true, dealType, dateFrom, dateTo, cryptoCurrency, fiatCurrency);
        } else {
            totalAmount = dealRepository.getTotalAmountSum(true, dealType, dateFrom, cryptoCurrency, fiatCurrency);
        }
        return Objects.nonNull(totalAmount)
                ? totalAmount.setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros()
                : BigDecimal.ZERO;
    }
}
