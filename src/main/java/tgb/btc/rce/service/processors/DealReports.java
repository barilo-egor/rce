package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CommandProcessor(command = Command.DEAL_REPORTS)
@Slf4j
public class DealReports extends Processor {

    private final static String TODAY = "За сегодня";
    private final static String TEN_DAYS = "За десять дней";
    public static final String MONTH = "За месяц";

    private final DealService dealService;

    @Autowired
    public DealReports(IResponseSender responseSender, UserService userService, DealService dealService) {
        super(responseSender, userService);
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        checkForCancel(update);
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
                                                .text("Назад")
                                                .build()
                                )));
                userService.nextStep(chatId, Command.DEAL_REPORTS);
                break;
            case 1:
                String period = UpdateUtil.getMessageText(update);
                switch (period) {
                    case TODAY:
                        loadReport(dealService.getByDate(LocalDate.now()), chatId, period);
                        break;
                    case TEN_DAYS:
                        loadReport(dealService.getByDateBetween(LocalDate.now().minusDays(10), LocalDate.now()), chatId, period);
                        break;
                    case MONTH:
                        loadReport(dealService.getByDateBetween(LocalDate.now().minusDays(30), LocalDate.now()), chatId, period);
                        break;
                    case "Назад":
                        processToAdminMainPanel(chatId);
                        break;
                    default:
                        responseSender.sendMessage(chatId, "Указанный период не определен.");
                        return;
                }
                processToAdminMainPanel(chatId);
                break;
        }
    }

    private void loadReport(List<Deal> deals, Long chatId, String period) {
        HSSFWorkbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Сделки " + period);

        Row headRow = sheet.createRow(0);
        sheet.setDefaultColumnWidth(30);
        Cell headCell = headRow.createCell(0);
        headCell.setCellValue("Кошелек");
        headCell = headRow.createCell(1);
        headCell.setCellValue("Дата, время");
        headCell = headRow.createCell(2);
        headCell.setCellValue("Сум.руб.");
        headCell = headRow.createCell(3);
        headCell.setCellValue("Сум.BTC");
        headCell = headRow.createCell(4);
        headCell.setCellValue("Оплата");
        headCell = headRow.createCell(4);
        headCell.setCellValue("ID");

        int i = 2;
        for (Deal deal : deals) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(deal.getWallet());
            cell = row.createCell(1);
            cell.setCellValue(deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            cell = row.createCell(2);
            cell.setCellValue(deal.getAmount().setScale(0, RoundingMode.FLOOR).toString());
            cell = row.createCell(3);
            cell.setCellValue(deal.getCryptoAmount().setScale(8, RoundingMode.FLOOR).stripTrailingZeros().toString());
            cell = row.createCell(4);
            cell.setCellValue(deal.getPaymentType().getDisplayName());
            cell = row.createCell(5);
            cell.setCellValue(deal.getUser().getChatId());
        }
        String fileName = LocalDate.now() + ".xlsx";
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
}