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
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@CommandProcessor(command = Command.USERS_REPORT)
@Slf4j
public class UsersReport extends Processor {

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public UsersReport(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    @Async
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, "Формирование отчета запущено.");
        responseSender.sendMessage(chatId, "Отчет придет после того, как сформируется. Это может занять некоторое время.");
        processToAdminMainPanel(chatId);
        try {
            HSSFWorkbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("Пользователи");

            Row headRow = sheet.createRow(0);
            sheet.setDefaultColumnWidth(30);
            Cell headCell = headRow.createCell(0);
            headCell.setCellValue("Chat ID");
            headCell = headRow.createCell(1);
            headCell.setCellValue("Username");
            headCell = headRow.createCell(2);
            headCell.setCellValue("Куплено BTC");
            headCell = headRow.createCell(3);
            headCell.setCellValue("Куплено LTC");
            headCell = headRow.createCell(4);
            headCell.setCellValue("Куплено USDT");
            headCell = headRow.createCell(5);
            headCell.setCellValue("Продано BTC");
            headCell = headRow.createCell(6);
            headCell.setCellValue("Продано LTC");
            headCell = headRow.createCell(7);
            headCell.setCellValue("Продано USDT");
            headCell = headRow.createCell(8);
            headCell.setCellValue("Потрачено рублей");

            int i = 2;
            for (User user : userService.findAll()) {
                Row row = sheet.createRow(i);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(user.getChatId());
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(StringUtils.defaultIfEmpty(user.getUsername(), "скрыт"));
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(BigDecimalUtil.roundNullSafe(
                        dealRepository.getUserCryptoAmountSum(user.getChatId(), CryptoCurrency.BITCOIN, DealType.BUY),
                        CryptoCurrency.BITCOIN.getScale()).toPlainString()
                );
                Cell cell4 = row.createCell(3);
                cell4.setCellValue(BigDecimalUtil.roundNullSafe(
                        dealRepository.getUserCryptoAmountSum(user.getChatId(), CryptoCurrency.LITECOIN, DealType.BUY),
                        CryptoCurrency.LITECOIN.getScale()).toPlainString()
                );
                Cell cell5 = row.createCell(4);
                cell5.setCellValue(BigDecimalUtil.roundNullSafe(
                        dealRepository.getUserCryptoAmountSum(user.getChatId(), CryptoCurrency.USDT, DealType.BUY),
                        CryptoCurrency.USDT.getScale()).toPlainString()
                );
                Cell cell6 = row.createCell(5);
                cell6.setCellValue(BigDecimalUtil.roundNullSafe(
                        dealRepository.getUserCryptoAmountSum(user.getChatId(), CryptoCurrency.BITCOIN, DealType.SELL),
                        CryptoCurrency.BITCOIN.getScale()).toPlainString()
                );
                Cell cell7 = row.createCell(6);
                cell7.setCellValue(BigDecimalUtil.roundNullSafe(
                        dealRepository.getUserCryptoAmountSum(user.getChatId(), CryptoCurrency.LITECOIN, DealType.SELL),
                        CryptoCurrency.LITECOIN.getScale()).toPlainString()
                );
                Cell cell8 = row.createCell(7);
                cell8.setCellValue(BigDecimalUtil.roundNullSafe(
                        dealRepository.getUserCryptoAmountSum(user.getChatId(), CryptoCurrency.USDT, DealType.SELL),
                        CryptoCurrency.USDT.getScale()).toPlainString()
                );
                Cell cell9 = row.createCell(8);
                cell9.setCellValue(BigDecimalUtil.roundNullSafe(
                        dealRepository.getUserAmountSum(user.getChatId(), DealType.BUY), 0).toPlainString()
                );
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
            log.error("Ошибка при выгрузке файла " + this.getClass().getSimpleName(), e);
            throw new BaseException("Ошибка при выгрузке файла: " + e.getMessage());
        }
    }
}
