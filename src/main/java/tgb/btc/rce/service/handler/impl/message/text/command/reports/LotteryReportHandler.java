package tgb.btc.rce.service.handler.impl.message.text.command.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.LotteryWin;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.ILotteryWinService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class LotteryReportHandler implements ITextCommandHandler {

    private final ILotteryWinService lotteryWinService;

    private final IResponseSender responseSender;

    public LotteryReportHandler(ILotteryWinService lotteryWinService, IResponseSender responseSender) {
        this.lotteryWinService = lotteryWinService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
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

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.LOTTERY_REPORT;
    }
}
