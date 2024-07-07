package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.IOException;

@CommandProcessor(command = Command.SYSTEM_MESSAGES)
@Slf4j
public class SystemMessages extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Измените нужные сообщения и отправьте исправленный файл. " +
                        "Обязательно закройте файл, перед тем как отправлять.");
                responseSender.sendFile(chatId, new File(PropertiesPath.MESSAGE_PROPERTIES.getFileName()));
                modifyUserService.nextStep(chatId, Command.SYSTEM_MESSAGES.name());
                break;
            case 1:
                if (!update.hasMessage() || !update.getMessage().hasDocument()) {
                    responseSender.sendMessage(chatId, "Отправьте файл или вернитесь в главное меню.");
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
            responseSender.downloadFile(update.getMessage().getDocument(), PropertiesPath.MESSAGE_BUFFER_PROPERTIES.getFileName());
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых сообщений: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых сообщений: " + e.getMessage());
            return;
        }
        try {
            FileUtils.delete(new File(PropertiesPath.MESSAGE_PROPERTIES.getFileName()));
        } catch (IOException e) {
            log.error("Ошибки при удалении " + PropertiesPath.MESSAGE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + PropertiesPath.MESSAGE_PROPERTIES.getFileName() + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(PropertiesPath.MESSAGE_BUFFER_PROPERTIES.getFile(), PropertiesPath.MESSAGE_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + PropertiesPath.MESSAGE_BUFFER_PROPERTIES.getFileName()
                    + " в " + PropertiesPath.MESSAGE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + PropertiesPath.MESSAGE_BUFFER_PROPERTIES.getFileName() + " в " + PropertiesPath.MESSAGE_PROPERTIES.getFileName());
            return;
        }
        PropertiesPath.MESSAGE_PROPERTIES.reload();
        responseSender.sendMessage(chatId, "Переменные обновлены.");
    }
}
