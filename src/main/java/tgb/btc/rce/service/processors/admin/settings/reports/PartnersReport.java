package tgb.btc.rce.service.processors.admin.settings.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.PARTNERS_REPORT)
@Slf4j
public class PartnersReport extends Processor {

    private IDealCountService dealCountService;

    @Autowired
    public void setDealCountService(IDealCountService dealCountService) {
        this.dealCountService = dealCountService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        List<User> users = readUserService.findAll().stream().filter(user -> !user.getReferralUsers().isEmpty()).collect(Collectors.toList());
        if (users.isEmpty()) {
            responseSender.sendMessage(chatId, "Нет пользователей с рефералами.");
            return;
        }

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
            headCell.setCellValue("Количество приглашенных");
            headCell = headRow.createCell(3);
            headCell.setCellValue("Количество активных рефералов");
            headCell = headRow.createCell(4);
            headCell.setCellValue("Начислено всего");
            headCell = headRow.createCell(5);
            headCell.setCellValue("Текущий баланс");

            users.sort(Comparator.comparing(User::getCharges));

            int i = 2;
            for (User user : users) {
                Row row = sheet.createRow(i);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(user.getChatId());
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(StringUtils.defaultIfEmpty(user.getUsername(), "скрыт"));
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(user.getReferralUsers().size());
                Cell cell4 = row.createCell(3);
                cell4.setCellValue((int) user.getReferralUsers().stream()
                        .filter(usr -> dealCountService.getCountConfirmedByUserChatId(usr.getChatId()) > 0).count());
                Cell cell5 = row.createCell(4);
                cell5.setCellValue(user.getCharges());
                Cell cell6 = row.createCell(5);
                cell6.setCellValue(user.getReferralBalance());
                i++;
            }
            String fileName = "users_referrals.xlsx";
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
