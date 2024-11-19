package tgb.btc.rce.service.processors.admin.settings;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.io.File;
import java.io.IOException;

@Slf4j
@CommandProcessor(command = Command.BOT_VARIABLES)
public class BotVariables extends Processor {

    private final VariablePropertiesReader variablePropertiesReader;

    public BotVariables(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                        "Обязательно закройте файл, перед тем как отправлять.", keyboardService.getReplyCancel());
                responseSender.sendFile(chatId, new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName()));
                modifyUserService.nextStep(chatId, Command.BOT_VARIABLES.name());
                break;
            case 1:
                if (!update.hasMessage() || !update.getMessage().hasDocument()) {
                    responseSender.sendMessage(chatId, "Отправьте файл или вернитесь в главное меню.", keyboardService.getReplyCancel());
                    return;
                }
                updateProperties(update);
                processToAdminMainPanel(chatId);
                break;
        }
    }

    private void updateProperties(Update update) {
        Long chatId = updateService.getChatId(update);
        try {
            responseSender.downloadFile(update.getMessage().getDocument(), PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName());
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых переменных: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых переменных: " + e.getMessage());
            return;
        }
        try {
            FileUtils.delete(new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName()));
        } catch (IOException e) {
            log.error("Ошибки при удалении " + PropertiesPath.VARIABLE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + PropertiesPath.VARIABLE_PROPERTIES.getFileName() + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFile(), new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName()));
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName()
                    + " в " + PropertiesPath.VARIABLE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName() + " в " + PropertiesPath.VARIABLE_PROPERTIES.getFileName());
            return;
        }
        variablePropertiesReader.reload();
        responseSender.sendMessage(chatId, "Переменные обновлены.");
    }
}
