package tgb.btc.rce.service.handler.impl.message.text.command.settings.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
@Slf4j
public class UsersDealsReportHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IReadUserService readUserService;

    private final IDealCountService dealCountService;

    public UsersDealsReportHandler(IResponseSender responseSender, IReadUserService readUserService,
                                   IDealCountService dealCountService) {
        this.responseSender = responseSender;
        this.readUserService = readUserService;
        this.dealCountService = dealCountService;
    }

    @Override
    @Async
    public void handle(Message message) {
        process(message.getChatId());
    }

    public void process(Long chatId) {
        try {
            HSSFWorkbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("Сделки");
            Row head = sheet.createRow(0);
            sheet.setDefaultColumnWidth(30);
            Cell headCell = head.createCell(0);
            headCell.setCellValue("Chat ID");
            headCell = head.createCell(1);
            headCell.setCellValue("Username");
            headCell = head.createCell(2);
            headCell.setCellValue("Количество совершенных сделок");

            int i = 2;

            List<User> users = readUserService.findAll();
            Map<Long, Long> map = new HashMap<>();
            users.forEach(user -> {
                Long count = dealCountService.getCountConfirmedByUserChatId(user.getChatId());
                if (Objects.isNull(count)) map.put(user.getChatId(), 0L);
                if (count != 0) map.put(user.getChatId(), count);
            });

            users = users.stream()
                    .filter(user -> Objects.nonNull(map.get(user.getChatId())))
                    .sorted(Comparator.comparing(user -> map.get(user.getChatId())))
                    .toList();

            for (User user : users) {
                Row row = sheet.createRow(i);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(user.getChatId());
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(StringUtils.defaultIfEmpty(user.getUsername(), "скрыт"));
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(map.get(user.getChatId()));
                i++;
            }

            String fileName = "UsersDeals.xlsx";
            FileOutputStream outputStream = new FileOutputStream(fileName);
            book.write(outputStream);
            book.close();
            outputStream.close();
            File file = new File(fileName);
            responseSender.sendFile(chatId, file);
            log.debug("Админ " + chatId + " выгрузил отчет по сделкам пользователей.");
            Files.delete(file.toPath());
        } catch (IOException e) {
            log.error("Ошибка при выгрузке файла " + this.getClass().getSimpleName(), e);
            throw new BaseException("Ошибка при выгрузке файла: " + e.getMessage());
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.USERS_DEALS_REPORT;
    }
}
