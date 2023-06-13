package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReportDealVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.USERS_REPORT)
@Slf4j
public class UsersReport extends Processor {

    private DealRepository dealRepository;

    private DealService dealService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Override
    @Async
    public void run(Update update) {
        log.info("Старт отчета по пользователям.");
        Long chatId = UpdateUtil.getChatId(update);
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
            for (FiatCurrency fiatCurrency : FiatCurrencyUtil.getFiatCurrencies()) {
                cellHeaders.add("Потрачено " + fiatCurrency.getCode());
            }
            Cell headCell;
            for (int i = 0; i < cellHeaders.size(); i++) {
                headCell = headRow.createCell(i);
                headCell.setCellValue(cellHeaders.get(i));
            }

            int i = 2;
            log.info("Загрузка пользователей");
            List<User> users = userRepository.getAllForUserReport();
            log.info("Загрузка сделок.");
            List<ReportDealVO> deals = dealService.findAllForUsersReport();
            Map<Long, List<ReportDealVO>> usersDeals = new HashMap<>();
            log.info("Сортировка сделок по пользователям.");
            for (User user : users) {
                usersDeals.put(user.getChatId(), deals.stream()
                        .filter(deal -> deal.getUserPid().equals(user.getPid()))
                        .collect(Collectors.toList())
                );
            }
            log.info("Начало заполнения данных.");
            int j = 0;
            for (User user : users) {
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
                for (FiatCurrency fiatCurrency : FiatCurrencyUtil.getFiatCurrencies()) {
                    cellHeaders.add("Потрачено " + fiatCurrency.getCode());
                    Cell cell = row.createCell(++cellCount);
                    BigDecimal userAmount = BigDecimal.ZERO;
                    for (ReportDealVO deal : userDeals) {
                        if (isErrorDeal(deal)) continue;
                        if (DealType.BUY.equals(deal.getDealType()) && fiatCurrency.equals(deal.getFiatCurrency()))
                            userAmount = userAmount.add(deal.getAmount());
                    }
                    cell.setCellValue(BigDecimalUtil.roundNullSafe(userAmount, 0).toPlainString());
                }
                i++;
                j++;
                if (j == 1) log.info("Первый пользователь обработан.");
                if (j % 100 == 0) log.info(j + "-я сотня пользователей обработана.");
            }
            log.info("Данные заполнены.");
            String fileName = "users.xlsx";
            FileOutputStream outputStream = new FileOutputStream(fileName);
            book.write(outputStream);
            book.close();
            outputStream.close();
            File file = new File(fileName);
            log.info("Файл создан.");
            responseSender.sendFile(chatId, file);
            log.debug("Админ " + chatId + " выгрузил отчет по пользователям.");
            if (file.delete()) log.trace("Файл успешно удален.");
            else log.trace("Файл не удален.");
        } catch (IOException e) {
            log.error("Ошибка при выгрузке отчета по пользователям. " + this.getClass().getSimpleName(), e);
            throw new BaseException("Ошибка при выгрузке файла: " + e.getMessage());
        }
        log.info("Конец отчета по пользователям.");
    }

    private boolean isErrorDeal(ReportDealVO reportDealVO) {
        return Objects.isNull(reportDealVO.getAmount()) || Objects.isNull(reportDealVO.getCryptoAmount())
                || Objects.isNull(reportDealVO.getDealType()) || Objects.isNull(reportDealVO.getCryptoCurrency())
                || Objects.isNull(reportDealVO.getFiatCurrency());
    }

    public void setUserCryptoAmount(Cell cell, BigDecimal cryptoAmount, CryptoCurrency cryptoCurrency) {
        cell.setCellValue(BigDecimalUtil.roundNullSafe(
                cryptoAmount,
                cryptoCurrency.getScale()).toPlainString()
        );
    }
}
