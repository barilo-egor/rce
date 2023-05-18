package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.io.File;
import java.io.IOException;

@CommandProcessor(command = Command.SYSTEM_MESSAGES)
@Slf4j
public class SystemMessages extends Processor {

    @Autowired
    public SystemMessages(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Измените нужные сообщения и отправьте исправленный файл. " +
                        "Обязательно закройте файл, перед тем как отправлять.");
                responseSender.sendFile(chatId, new File(FilePaths.MESSAGE_PROPERTIES));
                userService.nextStep(chatId, Command.SYSTEM_MESSAGES);
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
            responseSender.downloadFile(update.getMessage().getDocument(), FilePaths.MESSAGE_BUFFER_PROPERTIES);
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых сообщений: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых сообщений: " + e.getMessage());
            return;
        }
        try {
            MessagePropertiesUtil.validate(BotProperties.MESSAGE_BUFFER_PROPERTIES);
        } catch (PropertyValueNotFoundException e) {
            log.error(e.getMessage(), e);
            responseSender.sendMessage(chatId, e.getMessage());
            try {
                FileUtils.delete(BotProperties.MESSAGE_BUFFER_PROPERTIES.getFile());
            } catch (IOException ex) {
                log.error("Ошибки при удалении " + FilePaths.MESSAGE_BUFFER_PROPERTIES, ex);
                responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.MESSAGE_BUFFER_PROPERTIES + ":"
                        + ex.getMessage());
            }
            return;
        }
        try {
            FileUtils.delete(new File(FilePaths.MESSAGE_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибки при удалении " + FilePaths.MESSAGE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " + FilePaths.MESSAGE_PROPERTIES + ":"
                    + e.getMessage());
            return;
        }
        try {
            FileUtils.moveFile(BotProperties.MESSAGE_BUFFER_PROPERTIES.getFile(), BotProperties.MESSAGE_PROPERTIES.getFile());
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + " + FilePaths.MESSAGE_BUFFER_PROPERTIES
                    + " в " + FilePaths.MESSAGE_PROPERTIES, e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + FilePaths.MESSAGE_BUFFER_PROPERTIES + " в " + FilePaths.MESSAGE_PROPERTIES);
            return;
        }
        BotProperties.MESSAGE_PROPERTIES.reload();
        responseSender.sendMessage(chatId, "Переменные обновлены.");
    }
}
