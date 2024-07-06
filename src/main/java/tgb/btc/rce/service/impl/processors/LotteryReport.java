package tgb.btc.rce.service.impl.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.LotteryWin;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.ILotteryWinService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CommandProcessor(command = Command.LOTTERY_REPORT)
@Slf4j
public class LotteryReport extends Processor {

    private ILotteryWinService lotteryWinService;

    @Autowired
    public void setLotteryWinService(ILotteryWinService lotteryWinService) {
        this.lotteryWinService = lotteryWinService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<LotteryWin> lotteryWins = lotteryWinService.findAll();
        if (CollectionUtils.isEmpty(lotteryWins)) {
            responseSender.sendMessage(chatId, "Список выигрышей пуст.");
            return;
        }
        try {
            HSSFWorkbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("Выигрыши лотереи");

            Row headRow = sheet.createRow(0);
            sheet.setDefaultColumnWidth(30);
            Cell headCell = headRow.createCell(0);
            headCell.setCellValue("Chat ID");
            headCell = headRow.createCell(1);
            headCell.setCellValue("Username");
            headCell = headRow.createCell(2);
            headCell.setCellValue("Дата и время");

            int i = 2;
            for (LotteryWin lotteryWin : lotteryWins) {
                Row row = sheet.createRow(i);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(lotteryWin.getUser().getChatId());
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(StringUtils.defaultIfEmpty(lotteryWin.getUser().getUsername(), "скрыт"));
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(lotteryWin.getWonDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
            String fileName = "lotteryWins.xlsx";
            FileOutputStream outputStream = new FileOutputStream(fileName);
            book.write(outputStream);
            book.close();
            outputStream.close();
            File file = new File(fileName);
            responseSender.sendFile(chatId, file);
            log.debug("Админ " + chatId + " выгрузил отчет по выигрышам лотереи.");
            if (file.delete()) log.trace("Файл успешно удален.");
            else log.trace("Файл не удален.");
        } catch (Exception e) {
            log.error("Ошибка при выгрузке файла " + this.getClass().getSimpleName(), e);
            throw new BaseException("Ошибка при выгрузке файла: " + e.getMessage());
        }
    }

}
