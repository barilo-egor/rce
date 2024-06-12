package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.process.BackupService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.sender.IResponseSender;
import tgb.btc.rce.util.UpdateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CommandProcessor(command = Command.BACKUP_DB)
@Slf4j
public class BackupBD extends Processor {


    private BackupService backupService;

    private IResponseSender responseSender;

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired(required = false)
    public void setBackupService(BackupService backupService) {
        this.backupService = backupService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);

        String strChatIds = PropertiesPath.CONFIG_PROPERTIES.getString("backup.chatIds");
        if (StringUtils.isBlank(strChatIds)) {
            log.info("Не найден ни один chatId для рассылки ежедневного бэкапа.");
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
}
