package tgb.btc.rce.service.processors.bulkdiscounts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;

@Slf4j
@CommandProcessor(command = Command.BULK_DISCOUNTS)
public class BulkDiscounts extends Processor {

    @Autowired
    public BulkDiscounts(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                "Обязательно закройте файл, перед тем как отправлять.");
        responseSender.sendFile(chatId, new File(FilePaths.BULK_DISCOUNT_PROPERTIES));
        userService.nextStep(chatId, Command.BULK_DISCOUNTS);
    }
}