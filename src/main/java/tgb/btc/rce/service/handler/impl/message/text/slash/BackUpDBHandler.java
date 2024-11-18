package tgb.btc.rce.service.handler.impl.message.text.slash;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.process.BackupService;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BackUpDBHandler implements ISlashCommandHandler {

    private final IResponseSender responseSender;

    private final BackupService backupService;

    public BackUpDBHandler(IResponseSender responseSender, BackupService backupService) {
        this.responseSender = responseSender;
        this.backupService = backupService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();

        String strChatIds = PropertiesPath.CONFIG_PROPERTIES.getString("backup.chatIds");
        if (StringUtils.isBlank(strChatIds)) {
            log.info("Не найден ни один chatId доступных для выгрузки бэкапа.");
            return;
        }
        List<Long> chatIds = new ArrayList<>();
        for (String strChatId : strChatIds.split(",")) {
            try {
                chatIds.add(Long.parseLong(strChatId));
            } catch (NumberFormatException e) {
                log.error("Не получилось спарсить chatId={} для выгрузки бэкапа.", strChatIds);
            }
        }
        if (chatIds.stream().noneMatch(id -> id.equals(chatId))) {
            responseSender.sendMessage(chatId, "У вас нет прав на выгрузку бэкапа.");
            return;
        }

        if (Objects.isNull(backupService)) {
            responseSender.sendMessage(chatId, "Отсутствует бин BackUpService.");
            return;
        }
        backupService.backup(file -> responseSender.sendFile(chatId, file));
        log.debug("Пользователь chatId={} выгрузил бэкап БД вручную.", chatId);
        responseSender.sendMessage(chatId, "Процесс резервного копирования запущен. По окончанию вам будет отправлен файл.");
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.BACKUP_DB;
    }
}
