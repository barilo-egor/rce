package tgb.btc.rce.service.processors.admin.settings.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDateDealService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ICryptoCurrenciesDesignService;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CommandProcessor(command = Command.DEAL_REPORTS)
@Slf4j
public class DealReports extends Processor {

    private final static String TODAY = "За сегодня";
    private final static String TEN_DAYS = "За десять дней";
    private final static String MONTH = "За месяц";
    private final static String DATE = "За дату";

    private IDateDealService dateDealService;

    private IApiDealService apiDealService;

    private ICryptoCurrenciesDesignService cryptoCurrenciesDesignService;

    @Autowired
    public void setCryptoCurrenciesDesignService(ICryptoCurrenciesDesignService cryptoCurrenciesDesignService) {
        this.cryptoCurrenciesDesignService = cryptoCurrenciesDesignService;
    }

    @Autowired
    public void setDateDealService(IDateDealService dateDealService) {
        this.dateDealService = dateDealService;
    }

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Выберите период.",
                        keyboardBuildService.buildReply(2,
                                List.of(
                                        ReplyButton.builder()
                                                .text(TODAY)
                                                .build(),
                                        ReplyButton.builder()
                                                .text(TEN_DAYS)
                                                .build(),
                                        ReplyButton.builder()
                                                .text(MONTH)
                                                .build(),
                                        ReplyButton.builder()
                                                .text(DATE)
                                                .build(),
                                        ReplyButton.builder()
                                                .text(Command.ADMIN_BACK.getText())
                                                .build()
                                )));
                modifyUserService.nextStep(chatId, Command.DEAL_REPORTS.name());
                break;
            case 1:
                String period = UpdateUtil.getMessageText(update);
                switch (period) {
                    case TODAY:
                        try {
                            loadReport(dateDealService.getByDate(LocalDate.now()), chatId, period, apiDealService.getAcceptedByDate(LocalDateTime.now()));
                        } catch (Exception e) {
                            log.error("Ошибка при выгрузке отчета.", e);
                        }
                        break;
                    case TEN_DAYS:
                        try {
                            loadReport(dateDealService.getByDateBetween(LocalDate.now().minusDays(10), LocalDate.now()),
                                    chatId, period, apiDealService.getAcceptedByDateBetween(LocalDateTime.now().minusDays(10), LocalDateTime.now()));
                        } catch (Exception e) {
                            log.error("Ошибка при выгрузке отчета.", e);
                        }
                        break;
                    case MONTH:
                        try {
                            loadReport(dateDealService.getByDateBetween(LocalDate.now().minusDays(30), LocalDate.now()), chatId, period,
                                    apiDealService.getAcceptedByDateBetween(LocalDateTime.now().minusDays(30), LocalDateTime.now()));
                        } catch (Exception e) {
                            log.error("Ошибка при выгрузке отчета.", e);
                        }
                        break;
                    case DATE:
                        responseSender.sendMessage(chatId, "Введите дату в формате дд.мм.гггг");
                        modifyUserService.nextStep(chatId);
                        return;
                    case "Назад":
                        processToAdminMainPanel(chatId);
                        break;
                    default:
                        responseSender.sendMessage(chatId, "Указанный период не определен.");
                        return;
                }
                processToAdminMainPanel(chatId);
                break;
            case 2:
                try {
                    LocalDate date = getDate(update);
                    LocalDateTime dateTime = date.atStartOfDay();
                    loadReport(dateDealService.getByDate(date), chatId, date.format(DateTimeFormatter.ISO_DATE), apiDealService.getAcceptedByDate(dateTime));
                    processToAdminMainPanel(chatId);
                } catch (Exception e) {
                    responseSender.sendMessage(chatId, e.getMessage());
                }
                break;
        }
    }

    public LocalDate getDate(Update update) {
        if (!UpdateUtil.hasMessageText(update)) throw new BaseException("Отсутствует message text.");
        String[] values = UpdateUtil.getMessageText(update).split("\\.");
        try {
            if (values.length != 3) throw new BaseException("Неверный формат даты.");
            return LocalDate.of(Integer.parseInt(values[2]), Integer.parseInt(values[1]), Integer.parseInt(values[0]));
        } catch (DateTimeException e) {
            throw new BaseException("Неверный формат даты.");
        }
    }

    private void fillDealHeadCell(Cell headCell, Row headRow) {
        headCell.setCellValue("Тип сделки");
        headCell = headRow.createCell(1);
        headCell.setCellValue("Кошелек");
        headCell = headRow.createCell(2);
        headCell.setCellValue("Дата, время");
        headCell = headRow.createCell(3);
        headCell.setCellValue("Фиатная сумма");
        headCell = headRow.createCell(4);
        headCell.setCellValue("Фиатная валюта");
        headCell = headRow.createCell(5);
        headCell.setCellValue("Сумма крипты");
        headCell = headRow.createCell(6);
        headCell.setCellValue("Крипто валюта");
        headCell = headRow.createCell(7);
        headCell.setCellValue("Оплата");
        headCell = headRow.createCell(8);
        headCell.setCellValue("ID");
    }

    private void fillApiDealHeadCell(Cell headCell, Row headRow) {
        headCell.setCellValue("Тип сделки");
        headCell = headRow.createCell(1);
        headCell.setCellValue("Дата, время");
        headCell = headRow.createCell(2);
        headCell.setCellValue("Фиатная сумма");
        headCell = headRow.createCell(3);
        headCell.setCellValue("Фиатная валюта");
        headCell = headRow.createCell(4);
        headCell.setCellValue("Сумма крипты");
        headCell = headRow.createCell(5);
        headCell.setCellValue("Крипто валюта");
        headCell = headRow.createCell(6);
        headCell.setCellValue("ID");
    }

    private void loadReport(List<Deal> deals, Long chatId, String period, List<ApiDeal> apiDeals) {
        if (CollectionUtils.isEmpty(deals) && CollectionUtils.isEmpty(apiDeals)) {
            responseSender.sendMessage(chatId, "Сделки отсутствуют.");
            return;
        }
        HSSFWorkbook book = new HSSFWorkbook();
        if (CollectionUtils.isNotEmpty(deals)) {
            Sheet sheet = book.createSheet("Сделки " + period);
            fillDealSheet(sheet, deals);
        }
        if (CollectionUtils.isNotEmpty(apiDeals)) {
            Sheet apiSheet = book.createSheet("Api-сделки " + period);
            fillApiDealSheet(apiSheet, apiDeals);
        }
        String fileName = period + ".xls";
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            book.write(outputStream);
            book.close();
            outputStream.close();
            File file = new File(fileName);
            responseSender.sendFile(chatId, file);
            log.debug("Админ " + chatId + " выгрузил отчет по сделкам");
            if (file.delete()) log.trace("Файл успешно удален.");
            else log.trace("Файл не удален.");
        } catch (IOException t) {
            log.error("Ошибка при выгрузке файла. " + this.getClass().getSimpleName(), t);
            throw new BaseException();
        }
    }

    private void fillDealSheet(Sheet sheet, List<Deal> deals) {
        Row headRow = sheet.createRow(0);
        sheet.setDefaultColumnWidth(30);
        Cell headCell = headRow.createCell(0);
        fillDealHeadCell(headCell, headRow);

        int i = 2;
        Map<CryptoCurrency, BigDecimal> totalBuyCryptoAmountMap = new HashMap<>();
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalBuyCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));
        Map<CryptoCurrency, BigDecimal> totalSellCryptoAmountMap = new HashMap<>();
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalSellCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));

        Map<FiatCurrency, BigDecimal> totalBuyFiatAmountMap = new HashMap<>();
        Arrays.stream(FiatCurrency.values())
                .forEach(fiatCurrency -> totalBuyFiatAmountMap.put(fiatCurrency, BigDecimal.ZERO));
        Map<FiatCurrency, BigDecimal> totalSellFiatAmountMap = new HashMap<>();
        Arrays.stream(FiatCurrency.values())
                .forEach(fiatCurrency -> totalSellFiatAmountMap.put(fiatCurrency, BigDecimal.ZERO));
        for (Deal deal : deals) {
            Row row = sheet.createRow(i);
            boolean isBuy = DealType.isBuy(deal.getDealType());
            Cell cell = row.createCell(0);
            cell.setCellValue(deal.getDealType().name());
            cell = row.createCell(1);
            cell.setCellValue(deal.getWallet());
            cell = row.createCell(2);
            cell.setCellValue(deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            cell = row.createCell(3);
            cell.setCellValue(deal.getAmount().setScale(0, RoundingMode.FLOOR).toString());
            Map<FiatCurrency, BigDecimal> totalFiatAmountMap = isBuy
                    ? totalBuyFiatAmountMap
                    : totalSellFiatAmountMap;
            totalFiatAmountMap.put(deal.getFiatCurrency(), totalFiatAmountMap.get(deal.getFiatCurrency()).add(deal.getAmount()));
            cell = row.createCell(4);
            cell.setCellValue(deal.getFiatCurrency().getCode());
            cell = row.createCell(5);
            cell.setCellValue(BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()));
            Map<CryptoCurrency, BigDecimal> totalCryptoAmountMap = isBuy
                    ? totalBuyCryptoAmountMap : totalSellCryptoAmountMap;
            totalCryptoAmountMap.put(deal.getCryptoCurrency(), totalCryptoAmountMap.get(deal.getCryptoCurrency()).add(deal.getCryptoAmount()));
            cell = row.createCell(6);
            cell.setCellValue(cryptoCurrenciesDesignService.getDisplayName(deal.getCryptoCurrency()));
            cell = row.createCell(7);
            // getPaymentTypeEnum используется для старых сделок
            String paymentTypeName = Objects.nonNull(deal.getPaymentType())
                    ? deal.getPaymentType().getName()
                    : StringUtils.EMPTY;
            cell.setCellValue(paymentTypeName);
            cell = row.createCell(8);
            cell.setCellValue(deal.getUser().getChatId());
            i++;
        }
        i++;

        i += 1;

        FiatCurrency[] fiatCurrencies = FiatCurrency.values();
        CryptoCurrency[] cryptoCurrencies = CryptoCurrency.values();
        int fiatCurrenciesLength = fiatCurrencies.length;
        int cryptoCurrencyLength = cryptoCurrencies.length;
        int maxLength = Math.max(fiatCurrenciesLength, cryptoCurrencyLength);

        Row row = sheet.createRow(i);
        Cell cell = row.createCell(3);
        cell.setCellValue("Покупка");
        i = printTotal(sheet, i, totalBuyCryptoAmountMap, totalBuyFiatAmountMap, fiatCurrencies, cryptoCurrencies,
                fiatCurrenciesLength, cryptoCurrencyLength, maxLength, 3);
        i++;
        row = sheet.createRow(i);
        cell = row.createCell(3);
        cell.setCellValue("Продажа");
        printTotal(sheet, i, totalSellCryptoAmountMap, totalSellFiatAmountMap, fiatCurrencies, cryptoCurrencies,
                fiatCurrenciesLength, cryptoCurrencyLength, maxLength, 3);

    }

    private void fillApiDealSheet(Sheet apiSheet, List<ApiDeal> apiDeals) {
        Row headRow = apiSheet.createRow(0);
        apiSheet.setDefaultColumnWidth(30);
        Cell headCell = headRow.createCell(0);
        fillApiDealHeadCell(headCell, headRow);
        int i = 2;
        Map<CryptoCurrency, BigDecimal> totalBuyCryptoAmountMap = new HashMap<>();
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalBuyCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));
        Map<CryptoCurrency, BigDecimal> totalSellCryptoAmountMap = new HashMap<>();
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalSellCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));

        Map<FiatCurrency, BigDecimal> totalBuyFiatAmountMap = new HashMap<>();
        Arrays.stream(FiatCurrency.values())
                .forEach(fiatCurrency -> totalBuyFiatAmountMap.put(fiatCurrency, BigDecimal.ZERO));
        Map<FiatCurrency, BigDecimal> totalSellFiatAmountMap = new HashMap<>();
        Arrays.stream(FiatCurrency.values())
                .forEach(fiatCurrency -> totalSellFiatAmountMap.put(fiatCurrency, BigDecimal.ZERO));
        for (ApiDeal deal : apiDeals) {
            Row row = apiSheet.createRow(i);
            boolean isBuy = DealType.isBuy(deal.getDealType());
            Cell cell = row.createCell(0);
            cell.setCellValue(deal.getDealType().name());
            cell = row.createCell(1);
            cell.setCellValue(deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            cell = row.createCell(2);
            cell.setCellValue(deal.getAmount().setScale(0, RoundingMode.FLOOR).toString());
            Map<FiatCurrency, BigDecimal> totalFiatAmountMap = isBuy
                    ? totalBuyFiatAmountMap
                    : totalSellFiatAmountMap;
            totalFiatAmountMap.put(deal.getApiUser().getFiatCurrency(), totalFiatAmountMap.get(deal.getApiUser().getFiatCurrency()).add(deal.getAmount()));
            cell = row.createCell(3);
            cell.setCellValue(deal.getApiUser().getFiatCurrency().getCode());
            cell = row.createCell(4);
            cell.setCellValue(BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()));
            Map<CryptoCurrency, BigDecimal> totalCryptoAmountMap = isBuy
                    ? totalBuyCryptoAmountMap : totalSellCryptoAmountMap;
            totalCryptoAmountMap.put(deal.getCryptoCurrency(), totalCryptoAmountMap.get(deal.getCryptoCurrency()).add(deal.getCryptoAmount()));
            cell = row.createCell(5);
            cell.setCellValue(cryptoCurrenciesDesignService.getDisplayName(deal.getCryptoCurrency()));
            cell = row.createCell(6);
            cell.setCellValue(deal.getApiUser().getId());
            i++;
        }
        i++;

        i += 1;

        FiatCurrency[] fiatCurrencies = FiatCurrency.values();
        CryptoCurrency[] cryptoCurrencies = CryptoCurrency.values();
        int fiatCurrenciesLength = fiatCurrencies.length;
        int cryptoCurrencyLength = cryptoCurrencies.length;
        int maxLength = Math.max(fiatCurrenciesLength, cryptoCurrencyLength);
        Row row = apiSheet.createRow(i);
        Cell cell = row.createCell(2);
        cell.setCellValue("Покупка");
        i = printTotal(apiSheet, i, totalBuyCryptoAmountMap, totalBuyFiatAmountMap, fiatCurrencies, cryptoCurrencies,
                fiatCurrenciesLength, cryptoCurrencyLength, maxLength, 2);
        i++;
        row = apiSheet.createRow(i);
        cell = row.createCell(2);
        cell.setCellValue("Продажа");
        printTotal(apiSheet, i, totalSellCryptoAmountMap, totalSellFiatAmountMap, fiatCurrencies, cryptoCurrencies,
                fiatCurrenciesLength, cryptoCurrencyLength, maxLength, 2);
    }

    private int printTotal(Sheet sheet, int i, Map<CryptoCurrency, BigDecimal> totalBuyCryptoAmountMap,
                           Map<FiatCurrency, BigDecimal> totalBuyFiatAmountMap, FiatCurrency[] fiatCurrencies,
                           CryptoCurrency[] cryptoCurrencies, int fiatCurrenciesLength, int cryptoCurrencyLength,
                           int maxLength, int startCell) {
        Row row;
        i++;
        for (int j = 0; j < maxLength; j++) {
            row = sheet.createRow(i);
            if (j < fiatCurrenciesLength) printFiatTotal(row, fiatCurrencies[j], totalBuyFiatAmountMap, startCell);
            if (j < cryptoCurrencyLength)
                printCryptoTotal(row, cryptoCurrencies[j], totalBuyCryptoAmountMap, startCell + 2);
            i++;
        }
        return i;
    }

    private void printFiatTotal(Row row, FiatCurrency fiatCurrency, Map<FiatCurrency, BigDecimal> totalFiatAmountMap, int startCell) {
        Cell cell = row.createCell(startCell);
        cell.setCellValue(BigDecimalUtil.roundToPlainString(totalFiatAmountMap.get(fiatCurrency)));
        cell = row.createCell(startCell + 1);
        cell.setCellValue(fiatCurrency.getGenitive());
    }


    private void printCryptoTotal(Row row, CryptoCurrency cryptoCurrency, Map<CryptoCurrency, BigDecimal> totalFiatAmountMap, int startCell) {
        Cell cell = row.createCell(startCell);
        cell.setCellValue(BigDecimalUtil.roundToPlainString(totalFiatAmountMap.get(cryptoCurrency),
                cryptoCurrency.getScale()));
        cell = row.createCell(startCell + 1);
        cell.setCellValue(cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency));
    }
}
