package tgb.btc.rce.service.handler.util.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.ILoadReportService;
import tgb.btc.rce.service.util.ICryptoCurrenciesDesignService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class LoadReportService implements ILoadReportService {

    private final IResponseSender responseSender;

    private final IBigDecimalService bigDecimalService;

    private final ICryptoCurrenciesDesignService cryptoCurrenciesDesignService;

    public LoadReportService(IResponseSender responseSender, IBigDecimalService bigDecimalService,
                             ICryptoCurrenciesDesignService cryptoCurrenciesDesignService) {
        this.responseSender = responseSender;
        this.bigDecimalService = bigDecimalService;
        this.cryptoCurrenciesDesignService = cryptoCurrenciesDesignService;
    }

    @Override
    public void loadReport(List<Deal> deals, Long chatId, String period, List<ApiDeal> apiDeals) {
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
        String fileName = System.currentTimeMillis() + ".xls";
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            book.write(outputStream);
            book.close();
            outputStream.close();
            File file = new File(fileName);
            responseSender.sendFile(chatId, file);
            log.debug("Админ {} выгрузил отчет по сделкам", chatId);
            Files.delete(file.toPath());
        } catch (IOException t) {
            log.error("Ошибка при выгрузке файла. {}", this.getClass().getSimpleName(), t);
            throw new BaseException();
        }
    }

    private void fillDealSheet(Sheet sheet, List<Deal> deals) {
        Row headRow = sheet.createRow(0);
        sheet.setDefaultColumnWidth(30);
        Cell headCell = headRow.createCell(0);
        fillDealHeadCell(headCell, headRow);

        int i = 2;
        Map<CryptoCurrency, BigDecimal> totalBuyCryptoAmountMap = new EnumMap<>(CryptoCurrency.class);
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalBuyCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));
        Map<CryptoCurrency, BigDecimal> totalSellCryptoAmountMap = new EnumMap<>(CryptoCurrency.class);
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalSellCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));

        Map<FiatCurrency, BigDecimal> totalBuyFiatAmountMap = new EnumMap<>(FiatCurrency.class);
        Arrays.stream(FiatCurrency.values())
                .forEach(fiatCurrency -> totalBuyFiatAmountMap.put(fiatCurrency, BigDecimal.ZERO));
        Map<FiatCurrency, BigDecimal> totalSellFiatAmountMap = new EnumMap<>(FiatCurrency.class);
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
            cell.setCellValue(bigDecimalService.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()));
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
        Map<CryptoCurrency, BigDecimal> totalBuyCryptoAmountMap = new EnumMap<>(CryptoCurrency.class);
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalBuyCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));
        Map<CryptoCurrency, BigDecimal> totalSellCryptoAmountMap = new EnumMap<>(CryptoCurrency.class);
        Arrays.stream(CryptoCurrency.values())
                .forEach(cryptoCurrency -> totalSellCryptoAmountMap.put(cryptoCurrency, BigDecimal.ZERO));

        Map<FiatCurrency, BigDecimal> totalBuyFiatAmountMap = new EnumMap<>(FiatCurrency.class);
        Arrays.stream(FiatCurrency.values())
                .forEach(fiatCurrency -> totalBuyFiatAmountMap.put(fiatCurrency, BigDecimal.ZERO));
        Map<FiatCurrency, BigDecimal> totalSellFiatAmountMap = new EnumMap<>(FiatCurrency.class);
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
            cell.setCellValue(bigDecimalService.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()));
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
        cell.setCellValue(bigDecimalService.roundToPlainString(totalFiatAmountMap.get(fiatCurrency)));
        cell = row.createCell(startCell + 1);
        cell.setCellValue(fiatCurrency.getGenitive());
    }


    private void printCryptoTotal(Row row, CryptoCurrency cryptoCurrency, Map<CryptoCurrency, BigDecimal> totalFiatAmountMap, int startCell) {
        Cell cell = row.createCell(startCell);
        cell.setCellValue(bigDecimalService.roundToPlainString(totalFiatAmountMap.get(cryptoCurrency),
                cryptoCurrency.getScale()));
        cell = row.createCell(startCell + 1);
        cell.setCellValue(cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency));
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
}
