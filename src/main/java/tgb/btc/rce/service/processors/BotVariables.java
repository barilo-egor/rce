package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.IOException;

@Slf4j
@CommandProcessor(command = Command.BOT_VARIABLES)
public class BotVariables extends Processor {

    @Autowired
    public BotVariables(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendFile(chatId, new File(FilePaths.BOT_VARIABLE_PROPERTIES));
                responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                        "Обязательно закройте файл, перед тем как отправлять.");
                userService.nextStep(chatId, Command.BOT_VARIABLES);
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
            responseSender.downloadFile(update.getMessage().getDocument(), FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES);
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых переменных: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых переменных: " + e.getMessage());
            return;
        }
        File newProperties = new File(FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES);
        try {
            BotVariablePropertiesUtil.validate(newProperties);
        } catch (BaseException e) {
            log.error("Ошибка при чтении файла: ", e);
            responseSender.sendMessage(chatId, "Ошибка при чтении файла: " + e.getMessage());
            return;
        }
        try {
            FileUtils.delete(new File(FilePaths.BOT_VARIABLE_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибки при удалении " + FilePaths.BOT_VARIABLE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.BOT_VARIABLE_PROPERTIES + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(newProperties, new File(FilePaths.BOT_VARIABLE_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES
                    + " в " + FilePaths.BOT_VARIABLE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES + " в " + FilePaths.BOT_VARIABLE_PROPERTIES);
            return;
        }
        BotVariablePropertiesUtil.loadProperties();
        responseSender.sendMessage(chatId, "Переменные обновлены.");
    }
}
