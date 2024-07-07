package tgb.btc.rce.service.processors.admin.settings;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.IOException;

@Slf4j
@CommandProcessor(command = Command.BOT_VARIABLES)
public class BotVariables extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                        "Обязательно закройте файл, перед тем как отправлять.", BotKeyboard.REPLY_CANCEL);
                responseSender.sendFile(chatId, new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName()));
                modifyUserService.nextStep(chatId, Command.BOT_VARIABLES.name());
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
            responseSender.downloadFile(update.getMessage().getDocument(), PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName());
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых переменных: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых переменных: " + e.getMessage());
            return;
        }
        try {
            FileUtils.delete(PropertiesPath.VARIABLE_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при удалении " + PropertiesPath.VARIABLE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + PropertiesPath.VARIABLE_PROPERTIES.getFileName() + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFile(), PropertiesPath.VARIABLE_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName()
                    + " в " + PropertiesPath.VARIABLE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName() + " в " + PropertiesPath.VARIABLE_PROPERTIES.getFileName());
            return;
        }
        PropertiesPath.VARIABLE_PROPERTIES.reload();
        responseSender.sendMessage(chatId, "Переменные обновлены.");
    }
}
