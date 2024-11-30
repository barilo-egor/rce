package tgb.btc.rce.service.handler.impl.state.settings;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class BotVariablesStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final VariablePropertiesReader variablePropertiesReader;

    public BotVariablesStateHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                    IAdminPanelService adminPanelService, VariablePropertiesReader variablePropertiesReader) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().equals(TextCommand.CANCEL.getText())) {
            Long chatId = update.getMessage().getChatId();
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        if (!update.hasMessage() || !update.getMessage().hasDocument()) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Отправьте измененный файл, либо нажмите \""
                    + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Optional<Message> waitMessage = responseSender.sendMessage(chatId, "Обновление переменных, пожалуйста подождите.");
        if (!updateProperties(chatId, message.getDocument())) {
            return;
        }
        waitMessage.ifPresent(msg -> responseSender.deleteMessage(chatId, msg.getMessageId()));
        redisUserStateService.delete(chatId);
        responseSender.sendMessage(chatId, "Переменные обновлены.");
        adminPanelService.send(chatId);
    }

    private boolean updateProperties(Long chatId, Document document) {
        try {
            responseSender.downloadFile(document, PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName());
        } catch (IOException | TelegramApiException e) {
            log.error("Ошибка при скачивании новых переменных: ", e);
            responseSender.sendMessage(chatId, "Ошибка при скачивании новых переменных: " + e.getMessage());
            return false;
        }
        try {
            FileUtils.delete(new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName()));
        } catch (IOException e) {
            log.error("Ошибки при удалении {}", PropertiesPath.VARIABLE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при удалении " +
                    PropertiesPath.VARIABLE_PROPERTIES.getFileName() + ":" + e.getMessage());
            return false;
        }
        try {
            FileUtils.moveFile(new File(PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName()),
                    new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName()));
        } catch (IOException e) {
            log.error("Ошибки при перемещении файла + {} в {}", PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName(),
                    PropertiesPath.VARIABLE_PROPERTIES.getFileName(), e);
            responseSender.sendMessage(chatId, "Ошибки при перемещении файла + "
                    + PropertiesPath.VARIABLE_BUFFER_PROPERTIES.getFileName() + " в " + PropertiesPath.VARIABLE_PROPERTIES.getFileName());
            return false;
        }
        variablePropertiesReader.reload();
        return true;
    }

    @Override
    public UserState getUserState() {
        return UserState.BOT_VARIABLES;
    }
}
