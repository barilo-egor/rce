package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.USERS_DEALS_REPORT)
@Slf4j
public class UsersDealsReport extends Processor {
    private final DealService dealService;

    @Autowired
    public UsersDealsReport(IResponseSender responseSender, UserService userService, DealService dealService) {
        super(responseSender, userService);
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        try {
            HSSFWorkbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("Сделки");

            Row head = sheet.createRow(0);
            sheet.setDefaultColumnWidth(30);
            Cell headCell = head.createCell(0);
            headCell.setCellValue("Количество совершенных сделок");
            headCell = headCell.getRow().createCell(1);
            headCell.setCellValue("Username");

            int i = 2;

            List<User> users = userService.findAll().stream()
                    .filter(user -> dealService.getCountPassedByUserChatId(user.getChatId()) != 0)
                    .sorted((user1, user2) -> dealService.getCountPassedByUserChatId(user2.getChatId())
                            .compareTo(dealService.getDealsCountByUserChatId(user1.getChatId())))
                    .collect(Collectors.toList());
            for (User user : users) {
                Row row = sheet.createRow(i);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(user.getChatId());
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(StringUtils.defaultIfEmpty(user.getUsername(), "скрыт"));
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(dealService.getCountPassedByUserChatId(user.getChatId()));
                i++;
            }
            String fileName = "UsersDeals.xlsx";
            FileOutputStream outputStream = new FileOutputStream(fileName);
            book.write(outputStream);
            book.close();
            outputStream.close();
            File file = new File(fileName);
            Long chatId = UpdateUtil.getChatId(update);
            responseSender.sendFile(chatId, file);
            log.debug("Админ " + chatId + " выгрузил отчет по сделкам пользователей.");
            if (file.delete()) log.trace("Файл успешно удален.");
            else log.trace("Файл не удален.");
        } catch (IOException e) {
            log.error("Ошибка при выгрузке файла " + this.getClass().getSimpleName(), e);
            throw new BaseException("Ошибка при выгрузке файла: " + e.getMessage());
        }
    }
}
