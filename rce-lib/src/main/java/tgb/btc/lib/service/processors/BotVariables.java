package tgb.btc.lib.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.FilePaths;
import tgb.btc.lib.enums.BotKeyboard;
import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.exception.PropertyValueNotFoundException;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.io.File;
import java.io.IOException;

@Slf4j
@CommandProcessor(command = Command.BOT_VARIABLES)
public class BotVariables extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                        "Обязательно закройте файл, перед тем как отправлять.", BotKeyboard.REPLY_CANCEL);
                responseSender.sendFile(chatId, new File(FilePaths.BOT_VARIABLE_PROPERTIES));
                userService.nextStep(chatId, Command.BOT_VARIABLES);
                break;
            case 1:
                if (!update.hasMessage() || !update.getMessage().hasDocument()) {
                    responseSender.sendMessage(chatId, "Отправьте файл или вернитесь в главное меню.", BotKeyboard.REPLY_CANCEL);
                    return;
                }
                updateProperties(update);
                processToAdminMainPanel(chatId);
                break;
        }
    }

    private void updateProperties(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.downloadFile(update.getMessage().getDocument(), FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES);
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых переменных: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых переменных: " + e.getMessage());
            return;
        }
        try {
            BotProperties.BOT_VARIABLE_BUFFER_PROPERTIES.validate();
        } catch (PropertyValueNotFoundException e) {
            log.error(e.getMessage(),e);
            responseSender.sendMessage(chatId,e.getMessage());
            try {
                FileUtils.delete(BotProperties.BOT_VARIABLE_BUFFER_PROPERTIES.getFile());
            } catch (IOException ex) {
                log.error("Ошибки при удалении " + FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES, ex);
                responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES + ":"
                        + ex.getMessage());
            }
            return;
        }
        try {
            FileUtils.delete(BotProperties.BOT_VARIABLE_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при удалении " + FilePaths.BOT_VARIABLE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.BOT_VARIABLE_PROPERTIES + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(BotProperties.BOT_VARIABLE_BUFFER_PROPERTIES.getFile(), BotProperties.BOT_VARIABLE_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES
                    + " в " + FilePaths.BOT_VARIABLE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES + " в " + FilePaths.BOT_VARIABLE_PROPERTIES);
            return;
        }
        BotProperties.BOT_VARIABLE_PROPERTIES.reload();
        responseSender.sendMessage(chatId, "Переменные обновлены.");
    }
}
