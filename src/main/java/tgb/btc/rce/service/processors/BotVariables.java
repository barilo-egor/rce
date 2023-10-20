package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.constants.strings.FilePaths;
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
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                        "Обязательно закройте файл, перед тем как отправлять.", BotKeyboard.REPLY_CANCEL);
                responseSender.sendFile(chatId, new File(FilePaths.VARIABLE_PROPERTIES));
                userRepository.nextStep(chatId, Command.BOT_VARIABLES.name());
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
            responseSender.downloadFile(update.getMessage().getDocument(), FilePaths.VARIABLE_BUFFER_PROPERTIES);
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых переменных: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых переменных: " + e.getMessage());
            return;
        }
        try {
            FileUtils.delete(CommonProperties.VARIABLE.getFile());
        } catch (IOException e) {
            log.error("Ошибки при удалении " + FilePaths.VARIABLE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.VARIABLE_PROPERTIES + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(CommonProperties.VARIABLE_BUFFER.getFile(), CommonProperties.VARIABLE.getFile());
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + FilePaths.VARIABLE_BUFFER_PROPERTIES
                    + " в " + FilePaths.VARIABLE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + FilePaths.VARIABLE_BUFFER_PROPERTIES + " в " + FilePaths.VARIABLE_PROPERTIES);
            return;
        }
        CommonProperties.VARIABLE.reload();
        responseSender.sendMessage(chatId, "Переменные обновлены.");
    }
}
