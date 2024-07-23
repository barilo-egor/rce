package tgb.btc.rce.service.impl.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IReportDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.util.ICryptoCurrenciesDesignService;
import tgb.btc.rce.vo.DealReportData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class DealAutoReport {

    private IReportDealService reportDealService;

    private IDealCountService dealCountService;

    private IReadUserService readUserService;

    private INotifyService notifyService;

    public static LocalDate YESTERDAY;

    private ICryptoCurrenciesDesignService cryptoCurrenciesDesignService;
    
    private IFiatCurrencyService fiatCurrencyService;

    private IBigDecimalService bigDecimalService;

    @Autowired
    public void setDealCountService(IDealCountService dealCountService) {
        this.dealCountService = dealCountService;
    }

    @Autowired
    public void setBigDecimalService(IBigDecimalService bigDecimalService) {
        this.bigDecimalService = bigDecimalService;
    }

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setCryptoCurrenciesDesignService(ICryptoCurrenciesDesignService cryptoCurrenciesDesignService) {
        this.cryptoCurrenciesDesignService = cryptoCurrenciesDesignService;
    }

    @Autowired
    public void setNotifyService(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setReportDealService(IReportDealService reportDealService) {
        this.reportDealService = reportDealService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
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

            if (dealCountService.getCountConfirmedByDateTimeBetween(dateTimeBegin, dateTimeEnd) == 0) {
                notifyService.notifyMessage("Нет сделок за " + data.getPeriod() + ".", Set.of(UserRole.OPERATOR, UserRole.ADMIN));
                return;
            }
            Integer newUsersCount = readUserService.countByRegistrationDate(dateTimeBegin, dateTimeEnd);
            List<Long> allNewPartnersChatIds = readUserService.getChatIdsByRegistrationDateAndFromChatIdNotNull(
                    dateTimeBegin, dateTimeEnd);
            int allNewPartnersCount = (Objects.nonNull(allNewPartnersChatIds)
                                       ? allNewPartnersChatIds.size()
                                       : 0);

            int newActivePartnersCount = 0;

            for (Long chatId : allNewPartnersChatIds) {
                if (dealCountService.getCountConfirmedByUserChatId(chatId) > 0) {
                    newActivePartnersCount++;
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Отчет за ").append(data.getPeriod()).append(":\n");
            for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
                stringBuilder.append("Куплено ").append(cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency)).append(": ")
                        .append(getBuyCryptoAmount(cryptoCurrency, cryptoCurrency.getScale(), dateTimeBegin,
                                                   dateTimeEnd).toPlainString())
                        .append("\n");
            }
            stringBuilder.append("\n");
            Map<FiatCurrency, BigDecimal> totalAmounts = new HashMap<>();
            for (FiatCurrency fiatCurrency : fiatCurrencyService.getFiatCurrencies()) {
                BigDecimal totalSum = BigDecimal.ZERO;
                for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
                    BigDecimal cryptoAmount = getBuyAmount(cryptoCurrency, dateTimeBegin, dateTimeEnd,
                                                           fiatCurrency);
                    totalSum = totalSum.add(cryptoAmount);
                    stringBuilder.append("Получено ").append(fiatCurrency.getCode()).append(" от ")
                            .append(cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency)).append(": ")
                            .append(bigDecimalService.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())).append("\n");
                }
                totalAmounts.put(fiatCurrency, totalSum);
                stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
            for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
                BigDecimal cryptoAmount =
                        getSellCryptoAmount(cryptoCurrency, cryptoCurrency.getScale(), dateTimeBegin, dateTimeEnd);
                stringBuilder.append("Продано ").append(cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency)).append(": ")
                        .append(cryptoAmount.toPlainString())
                        .append("\n");
            }
            stringBuilder.append("\n");
            for (FiatCurrency fiatCurrency : fiatCurrencyService.getFiatCurrencies()) {
                stringBuilder.append("Всего получено ").append(fiatCurrency.getCode()).append(" : ")
                        .append(totalAmounts.get(fiatCurrency).toPlainString()).append("\n");
            }
            stringBuilder.append("\n" + "Количество новых пользователей: ").append(newUsersCount).append("\n")
                    .append("Количество новых партнеров: ").append(allNewPartnersCount).append("\n")
                    .append("Количество активных новых партнеров: ").append(newActivePartnersCount);
            notifyService.notifyMessage(stringBuilder.toString(), Set.of(UserRole.ADMIN));
        } catch (Exception e) {
            String message = "Ошибка при формировании периодического отчета за " + data.getPeriod() + ":\n"
                    + e.getMessage() + "\n"
                    + ExceptionUtils.getFullStackTrace(e);
            notifyService.notifyMessage(message, Set.of(UserRole.ADMIN));
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
        BigDecimal totalCryptoAmount = reportDealService.getCryptoAmountSum(dealType, dateFrom, dateTo,
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
            totalAmount = reportDealService.getTotalAmountSum(dealType, dateFrom, dateTo, cryptoCurrency,
                                                           fiatCurrency);
        } else {
            totalAmount = reportDealService.getTotalAmountSum(dealType, dateFrom, cryptoCurrency, fiatCurrency);
        }
        return Objects.nonNull(totalAmount)
               ? totalAmount.setScale(0, RoundingMode.HALF_DOWN).stripTrailingZeros()
               : BigDecimal.ZERO;
    }

}
