package tgb.btc.rce.service.processors;

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
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.service.Processor;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessageTextUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;
import tgb.btc.rce.web.util.CryptoCurrenciesDesignUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CommandProcessor(command = Command.DEAL_REPORTS)
@Slf4j
public class DealReports extends Processor {

    private final static String TODAY = "За сегодня";
    private final static String TEN_DAYS = "За десять дней";
    private final static String MONTH = "За месяц";
    private final static String DATE = "За дату";

    private DealService dealService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Выберите период.",
                        KeyboardUtil.buildReply(2,
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
                userRepository.nextStep(chatId, Command.DEAL_REPORTS.name());
                break;
            case 1:
                String period = UpdateUtil.getMessageText(update);
                switch (period) {
                    case TODAY:
                        try {
                            loadReport(dealService.getByDate(LocalDate.now()), chatId, period);
                        } catch (Exception e) {
                            log.error("ОШибка при выгрузке отчета.", e);
                        }
                        break;
                    case TEN_DAYS:
                        try {
                            loadReport(dealService.getByDateBetween(LocalDate.now().minusDays(10), LocalDate.now()), chatId, period);
                        } catch (Exception e) {
                            log.error("ОШибка при выгрузке отчета.", e);
                        }
                        break;
                    case MONTH:
                        try {
                            loadReport(dealService.getByDateBetween(LocalDate.now().minusDays(30), LocalDate.now()), chatId, period);
                        } catch (Exception e) {
                            log.error("ОШибка при выгрузке отчета.", e);
                        }
                        break;
                    case DATE:
                        responseSender.sendMessage(chatId, "Введите дату в формате дд.мм.гггг");
                        userService.nextStep(chatId);
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
                    LocalDate date = MessageTextUtil.getDate(update);
                    loadReport(dealService.getByDate(date), chatId, date.format(DateTimeFormatter.ISO_DATE));
                    processToAdminMainPanel(chatId);
                } catch (Exception e) {
                    responseSender.sendMessage(chatId, e.getMessage());
                }
                break;
        }
    }

    private void loadReport(List<Deal> deals, Long chatId, String period) {
        if (CollectionUtils.isEmpty(deals)) {
            responseSender.sendMessage(chatId, "Сделки отсутствуют.");
            return;
        }
        HSSFWorkbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Сделки " + period);

        Row headRow = sheet.createRow(0);
        sheet.setDefaultColumnWidth(30);
        Cell headCell = headRow.createCell(0);
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
            cell.setCellValue(CryptoCurrenciesDesignUtil.getDisplayName(deal.getCryptoCurrency()));
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
                fiatCurrenciesLength, cryptoCurrencyLength, maxLength);
        i++;
        row = sheet.createRow(i);
        cell = row.createCell(3);
        cell.setCellValue("Продажа");
        printTotal(sheet, i, totalSellCryptoAmountMap, totalSellFiatAmountMap, fiatCurrencies, cryptoCurrencies,
                fiatCurrenciesLength, cryptoCurrencyLength, maxLength);

        String fileName = period + ".xlsx";
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

    private int printTotal(Sheet sheet, int i, Map<CryptoCurrency, BigDecimal> totalBuyCryptoAmountMap,
                           Map<FiatCurrency, BigDecimal> totalBuyFiatAmountMap, FiatCurrency[] fiatCurrencies,
                           CryptoCurrency[] cryptoCurrencies, int fiatCurrenciesLength, int cryptoCurrencyLength,
                           int maxLength) {
        Row row;
        i++;
        for (int j = 0; j < maxLength; j++) {
            row = sheet.createRow(i);
            if (j < fiatCurrenciesLength) printFiatTotal(row, fiatCurrencies[j], totalBuyFiatAmountMap);
            if (j < cryptoCurrencyLength) printCryptoTotal(row, cryptoCurrencies[j], totalBuyCryptoAmountMap);
            i++;
        }
        return i;
    }

    private void printFiatTotal(Row row, FiatCurrency fiatCurrency, Map<FiatCurrency, BigDecimal> totalFiatAmountMap) {
        Cell cell = row.createCell(3);
        cell.setCellValue(BigDecimalUtil.roundToPlainString(totalFiatAmountMap.get(fiatCurrency)));
        cell = row.createCell(4);
        cell.setCellValue(fiatCurrency.getGenitive());
    }


    private void printCryptoTotal(Row row, CryptoCurrency cryptoCurrency, Map<CryptoCurrency, BigDecimal> totalFiatAmountMap) {
        Cell cell = row.createCell(5);
        cell.setCellValue(BigDecimalUtil.roundToPlainString(totalFiatAmountMap.get(cryptoCurrency),
                cryptoCurrency.getScale()));
        cell = row.createCell(6);
        cell.setCellValue(CryptoCurrenciesDesignUtil.getDisplayName(cryptoCurrency));
    }
}
