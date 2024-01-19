package tgb.btc.rce.service.processors.bulkdiscounts;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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
        responseSender.sendFile(chatId, new File(PropertiesPath.BULK_DISCOUNT_PROPERTIES.getFileName()));
        userRepository.nextStep(chatId, Command.BULK_DISCOUNTS.name());
    }
}
