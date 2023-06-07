package tgb.btc.lib.service.processors.bulkdiscounts;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.FilePaths;
import tgb.btc.lib.enums.BotKeyboard;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.io.File;

@Slf4j
@CommandProcessor(command = Command.BULK_DISCOUNTS)
public class BulkDiscounts extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                "Обязательно закройте файл, перед тем как отправлять.", BotKeyboard.REPLY_CANCEL);
        responseSender.sendFile(chatId, new File(FilePaths.BULK_DISCOUNT_PROPERTIES));
        userService.nextStep(chatId, Command.BULK_DISCOUNTS);
    }
}
