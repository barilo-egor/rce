package tgb.btc.rce.service.processors.admin.settings.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IReportDealService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.report.ReportDealVO;
import tgb.btc.rce.vo.report.ReportUserVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.USERS_REPORT)
@Slf4j
public class UsersReport extends Processor {

    private IReportDealService reportDealService;

    private IFiatCurrencyService fiatCurrencyService;

    private IBigDecimalService bigDecimalService;

    @Autowired
    public void setBigDecimalService(IBigDecimalService bigDecimalService) {
        this.bigDecimalService = bigDecimalService;
    }

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setReportDealService(IReportDealService reportDealService) {
        this.reportDealService = reportDealService;
    }

    @Override
    @Async
    public void run(Update update) {
        log.info("Старт отчета по пользователям.");
        Long chatId = updateService.getChatId(update);
        responseSender.sendMessage(chatId, "Формирование отчета запущено.");
        responseSender.sendMessage(chatId, "Отчет придет после того, как сформируется. Это может занять некоторое время.");
        processToAdminMainPanel(chatId);
        try {
            HSSFWorkbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("Пользователи");
            Row headRow = sheet.createRow(0);
            sheet.setDefaultColumnWidth(30);
            List<String> cellHeaders = new ArrayList<>(List.of("Chat ID", "Username", "Куплено BTC", "Куплено LTC", "Куплено USDT",
                    "Куплено MONERO", "Продано BTC", "Продано LTC", "Продано USDT",
                    "Продано MONERO"));
            for (FiatCurrency fiatCurrency : fiatCurrencyService.getFiatCurrencies()) {
                cellHeaders.add("Потрачено " + fiatCurrency.getCode());
            }
            Cell headCell;
            for (int i = 0; i < cellHeaders.size(); i++) {
                headCell = headRow.createCell(i);
                headCell.setCellValue(cellHeaders.get(i));
            }

            int i = 2;
            List<Object[]> rawsUsers = readUserService.findAllForUsersReport();
            List<ReportUserVO> users = new ArrayList<>();
            for (Object[] raw : rawsUsers) {
                users.add(ReportUserVO.builder()
                        .pid((Long) raw[0])
                        .chatId((Long) raw[1])
                        .username((String) raw[2])
                        .build());
            }
            List<Object[]> raws = reportDealService.findAllForUsersReport();
            List<ReportDealVO> deals = new ArrayList<>();
            for (Object[] raw : raws) {
                deals.add(ReportDealVO.builder()
                        .pid((Long) raw[0])
                        .userPid((Long) raw[1])
                        .dealType((DealType) raw[2])
                        .cryptoCurrency((CryptoCurrency) raw[3])
                        .cryptoAmount((BigDecimal) raw[4])
                        .fiatCurrency((FiatCurrency) raw[5])
                        .amount((BigDecimal) raw[6])
                        .build());
            }
            Map<Long, List<ReportDealVO>> usersDeals = new HashMap<>();
            for (ReportUserVO user : users) {
                usersDeals.put(user.getChatId(), deals.stream()
                        .filter(deal -> deal.getUserPid().equals(user.getPid()))
                        .collect(Collectors.toList())
                );
            }
            for (ReportUserVO user : users) {
                int cellCount = 0;
                Row row = sheet.createRow(i);
                Cell cell1 = row.createCell(cellCount);
                cell1.setCellValue(user.getChatId());
                Cell cell2 = row.createCell(++cellCount);
                cell2.setCellValue(StringUtils.defaultIfEmpty(user.getUsername(), "скрыт"));
                List<CryptoCurrency> cryptoCurrencies = List.of(CryptoCurrency.values());
                List<ReportDealVO> userDeals = usersDeals.get(user.getChatId());
                for (CryptoCurrency cryptoCurrency : cryptoCurrencies) {
                    Cell cell = row.createCell(++cellCount);
                    BigDecimal cryptoAmount = BigDecimal.ZERO;
                    for (ReportDealVO deal : userDeals) {
                        if (isErrorDeal(deal)) continue;
                        if (DealType.BUY.equals(deal.getDealType()) && cryptoCurrency.equals(deal.getCryptoCurrency()))
                            cryptoAmount = cryptoAmount.add(deal.getCryptoAmount());
                    }
                    setUserCryptoAmount(cell, cryptoAmount, cryptoCurrency);
                }
                for (CryptoCurrency cryptoCurrency : cryptoCurrencies) {
                    Cell cell = row.createCell(++cellCount);
                    BigDecimal cryptoAmount = BigDecimal.ZERO;
                    for (ReportDealVO deal : userDeals) {
                        if (isErrorDeal(deal)) continue;
                        if (DealType.SELL.equals(deal.getDealType()) && cryptoCurrency.equals(deal.getCryptoCurrency()))
                            cryptoAmount = cryptoAmount.add(deal.getCryptoAmount());
                    }
                    setUserCryptoAmount(cell, cryptoAmount, cryptoCurrency);
                }
                for (FiatCurrency fiatCurrency : fiatCurrencyService.getFiatCurrencies()) {
                    cellHeaders.add("Потрачено " + fiatCurrency.getCode());
                    Cell cell = row.createCell(++cellCount);
                    BigDecimal userAmount = BigDecimal.ZERO;
                    for (ReportDealVO deal : userDeals) {
                        if (isErrorDeal(deal)) continue;
                        if (DealType.BUY.equals(deal.getDealType()) && fiatCurrency.equals(deal.getFiatCurrency()))
                            userAmount = userAmount.add(deal.getAmount());
                    }
                    cell.setCellValue(bigDecimalService.roundNullSafe(userAmount, 0).toPlainString());
                }
                i++;
            }
            String fileName = "users.xlsx";
            FileOutputStream outputStream = new FileOutputStream(fileName);
            book.write(outputStream);
            book.close();
            outputStream.close();
            File file = new File(fileName);
            responseSender.sendFile(chatId, file);
            log.debug("Админ " + chatId + " выгрузил отчет по пользователям.");
            if (file.delete()) log.trace("Файл успешно удален.");
            else log.trace("Файл не удален.");
        } catch (IOException e) {
            log.error("Ошибка при выгрузке отчета по пользователям. " + this.getClass().getSimpleName(), e);
            throw new BaseException("Ошибка при выгрузке файла: " + e.getMessage());
        }
    }

    private boolean isErrorDeal(ReportDealVO reportDealVO) {
        return Objects.isNull(reportDealVO.getAmount()) || Objects.isNull(reportDealVO.getCryptoAmount())
                || Objects.isNull(reportDealVO.getDealType()) || Objects.isNull(reportDealVO.getCryptoCurrency())
                || Objects.isNull(reportDealVO.getFiatCurrency());
    }

    public void setUserCryptoAmount(Cell cell, BigDecimal cryptoAmount, CryptoCurrency cryptoCurrency) {
        cell.setCellValue(bigDecimalService.roundNullSafe(
                cryptoAmount,
                cryptoCurrency.getScale()).toPlainString()
        );
    }
}
