package tgb.btc.rce.service.processors.bulkdiscounts;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.exception.PropertyValueNotFoundException;
import tgb.btc.library.vo.BulkDiscount;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import static tgb.btc.library.util.BulkDiscountUtil.BULK_DISCOUNTS;


@CommandProcessor(command = Command.BULK_DISCOUNTS, step = 1)
@Slf4j
public class UpdateBulkDiscounts extends Processor { // TODO удалить

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
            responseSender.downloadFile(update.getMessage().getDocument(), PropertiesPath.BULK_DISCOUNT_BUFFER_PROPERTIES.getFileName());
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании оптовых скидок: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании оптовых скидок: " + e.getMessage());
            return;
        }
        try {
            FileUtils.delete(PropertiesPath.BULK_DISCOUNT_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при удалении " + PropertiesPath.BULK_DISCOUNT_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + PropertiesPath.BULK_DISCOUNT_PROPERTIES.getFileName() + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(PropertiesPath.BULK_DISCOUNT_BUFFER_PROPERTIES.getFile(), PropertiesPath.BULK_DISCOUNT_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + PropertiesPath.BULK_DISCOUNT_BUFFER_PROPERTIES.getFileName()
                    + " в " + PropertiesPath.BULK_DISCOUNT_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + PropertiesPath.BULK_DISCOUNT_BUFFER_PROPERTIES.getFileName() + " в " + PropertiesPath.BULK_DISCOUNT_PROPERTIES.getFileName());
            return;
        }
        PropertiesPath.BULK_DISCOUNT_PROPERTIES.reload();
        BULK_DISCOUNTS.clear();
        for (String key : PropertiesPath.BULK_DISCOUNT_PROPERTIES.getKeys()) {
            int sum;
            if (StringUtils.isBlank(key)) {
                throw new PropertyValueNotFoundException("Не указано название для одного из ключей" + key + ".");
            }
            try {
                sum = Integer.parseInt(key.split("\\.")[2]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное название для ключа " + key + ".");
            }
            String value = PropertiesPath.BULK_DISCOUNT_PROPERTIES.getString(key);
            if (StringUtils.isBlank(value)) {
                throw new PropertyValueNotFoundException("Не указано значение для ключа " + key + ".");
            }
            double percent;
            try {
                percent = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное значение для ключа " + key + ".");
            }
            BULK_DISCOUNTS.add(BulkDiscount.builder()
                    .percent(percent)
                    .sum(sum)
                    .fiatCurrency(FiatCurrency.getByCode(key.split("\\.")[0]))
                    .dealType(DealType.findByKey((key.split("\\.")[1])))
                    .build());
        }
        BULK_DISCOUNTS.sort(Comparator.comparingInt(BulkDiscount::getSum));
        Collections.reverse(BULK_DISCOUNTS);
        responseSender.sendMessage(chatId, "Оптовые скидки обновлены.");
    }
}
