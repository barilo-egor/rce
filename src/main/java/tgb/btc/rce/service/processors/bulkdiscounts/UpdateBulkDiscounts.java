package tgb.btc.rce.service.processors.bulkdiscounts;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.BulkDiscountUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.IOException;

@CommandProcessor(command = Command.BULK_DISCOUNTS, step = 1)
@Slf4j
public class UpdateBulkDiscounts extends Processor {

    public UpdateBulkDiscounts(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasMessage() || !update.getMessage().hasDocument()) {
            responseSender.sendMessage(chatId, "Отправьте файл или вернитесь в главное меню.");
            return;
        }
        updateProperties(update);
        processToAdminMainPanel(chatId);
    }

    private void updateProperties(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.downloadFile(update.getMessage().getDocument(), FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES);
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании оптовых скидок: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании оптовых скидок: " + e.getMessage());
            return;
        }
        File newProperties = new File(FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES);
        try {
            BotVariablePropertiesUtil.validate(newProperties);
        } catch (BaseException e) {
            log.error("Ошибка при чтении файла: ", e);
            responseSender.sendMessage(chatId, "Ошибка при чтении файла: " + e.getMessage());
            return;
        }
        try {
            FileUtils.delete(new File(FilePaths.BULK_DISCOUNT_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибки при удалении " + FilePaths.BULK_DISCOUNT_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.BULK_DISCOUNT_PROPERTIES + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(newProperties, new File(FilePaths.BULK_DISCOUNT_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES
                    + " в " + FilePaths.BULK_DISCOUNT_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES + " в " + FilePaths.BULK_DISCOUNT_PROPERTIES);
            return;
        }
        BulkDiscountUtil.load();
        responseSender.sendMessage(chatId, "Оптовые скидки обновлены.");
    }
}
