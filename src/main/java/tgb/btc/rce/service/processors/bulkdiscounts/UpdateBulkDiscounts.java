package tgb.btc.rce.service.processors.bulkdiscounts;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.BulkDiscountUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.io.IOException;
import java.util.Map;

@CommandProcessor(command = Command.BULK_DISCOUNTS, step = 1)
@Slf4j
public class UpdateBulkDiscounts extends Processor {

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
        Map<Integer, Double> discounts;
        try {
            discounts = BulkDiscountUtil.validate(BotProperties.BULK_DISCOUNT_BUFFER_PROPERTIES);
        } catch (PropertyValueNotFoundException e) {
            log.error(e.getMessage(), e);
            responseSender.sendMessage(chatId, e.getMessage());
            try {
                FileUtils.delete(BotProperties.BULK_DISCOUNT_BUFFER_PROPERTIES.getFile());
            } catch (IOException ex) {
                log.error("Ошибки при удалении " + FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES, e);
                responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES + ":"
                        + e.getMessage());
            }
            return;
        }
        try {
            FileUtils.delete(BotProperties.BULK_DISCOUNT_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при удалении " + FilePaths.BULK_DISCOUNT_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.BULK_DISCOUNT_PROPERTIES + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(BotProperties.BULK_DISCOUNT_BUFFER_PROPERTIES.getFile(), BotProperties.BULK_DISCOUNT_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES
                    + " в " + FilePaths.BULK_DISCOUNT_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES + " в " + FilePaths.BULK_DISCOUNT_PROPERTIES);
            return;
        }
        BotProperties.BULK_DISCOUNT_PROPERTIES.reload();
        BulkDiscountUtil.load(discounts);
        responseSender.sendMessage(chatId, "Оптовые скидки обновлены.");
    }
}
