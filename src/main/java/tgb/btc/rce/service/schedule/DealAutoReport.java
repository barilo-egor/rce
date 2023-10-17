package tgb.btc.rce.service.schedule;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.repository.bot.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.vo.DealReportData;
import tgb.btc.rce.web.util.CryptoCurrenciesDesignUtil;

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

    public AdminService adminService;
    public static LocalDate YESTERDAY;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

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
        LocalDate lastDay = LocalDate.now().minusDays(1)
                .withDayOfMonth(firstDay.getMonth().length(firstDay.isLeapYear()));
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
                adminService.notify("Нет сделок за " + data.getPeriod() + ".");
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
                if (dealRepository.getCountPassedByChatId(chatId) > 0) {
                    newActivePartnersCount++;
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Отчет за ").append(data.getPeriod()).append(":\n");
            for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
                stringBuilder.append("Куплено ").append(CryptoCurrenciesDesignUtil.getDisplayName(cryptoCurrency)).append(": ")
                        .append(getBuyCryptoAmount(cryptoCurrency, cryptoCurrency.getScale(), dateTimeBegin,
                                                   dateTimeEnd).toPlainString())
                        .append("\n");
            }
            stringBuilder.append("\n");
            Map<FiatCurrency, BigDecimal> totalAmounts = new HashMap<>();
            for (FiatCurrency fiatCurrency : FiatCurrencyUtil.getFiatCurrencies()) {
                BigDecimal totalSum = BigDecimal.ZERO;
                for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
                    BigDecimal cryptoAmount = getBuyAmount(cryptoCurrency, dateTimeBegin, dateTimeEnd,
                                                           fiatCurrency);
                    totalSum = totalSum.add(cryptoAmount);
                    stringBuilder.append("Получено ").append(fiatCurrency.getCode()).append(" от ")
                            .append(CryptoCurrenciesDesignUtil.getDisplayName(cryptoCurrency)).append(": ")
                            .append(BigDecimalUtil.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())).append("\n");
                }
                totalAmounts.put(fiatCurrency, totalSum);
                stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
            for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
                BigDecimal cryptoAmount =
                        getSellCryptoAmount(cryptoCurrency, cryptoCurrency.getScale(), dateTimeBegin, dateTimeEnd);
                stringBuilder.append("Продано ").append(CryptoCurrenciesDesignUtil.getDisplayName(cryptoCurrency)).append(": ")
                        .append(cryptoAmount.toPlainString())
                        .append("\n");
            }
            stringBuilder.append("\n");
            for (FiatCurrency fiatCurrency : FiatCurrencyUtil.getFiatCurrencies()) {
                stringBuilder.append("Всего получено ").append(fiatCurrency.getCode()).append(" : ")
                        .append(totalAmounts.get(fiatCurrency).toPlainString()).append("\n");
            }
            stringBuilder.append("\n" + "Количество новых пользователей: ").append(newUsersCount).append("\n")
                    .append("Количество новых партнеров: ").append(allNewPartnersCount).append("\n")
                    .append("Количество активных новых партнеров: ").append(newActivePartnersCount);
            userRepository.getAdminsChatIds()
                    .forEach(chatId -> responseSender.sendMessage(chatId, stringBuilder.toString()));
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
        BigDecimal totalCryptoAmount = dealRepository.getCryptoAmountSum(true, dealType, dateFrom, dateTo,
                                                                         cryptoCurrency);
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
            totalAmount = dealRepository.getTotalAmountSum(true, dealType, dateFrom, dateTo, cryptoCurrency,
                                                           fiatCurrency);
        } else {
            totalAmount = dealRepository.getTotalAmountSum(true, dealType, dateFrom, cryptoCurrency, fiatCurrency);
        }
        return Objects.nonNull(totalAmount)
               ? totalAmount.setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros()
               : BigDecimal.ZERO;
    }

}
