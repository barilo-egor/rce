package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.process.BackupService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@CommandProcessor(command = Command.BACKUP_DB)
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
        if (Objects.isNull(backupService)) {
            responseSender.sendMessage(chatId, "Отсутствует бин BackUpService.");
            return;
        }
        backupService.backup();
        responseSender.sendMessage(chatId, "Процесс резервного копирования запущен");
    }
}
