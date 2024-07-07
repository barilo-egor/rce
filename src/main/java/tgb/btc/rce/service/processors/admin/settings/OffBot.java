package tgb.btc.rce.service.processors.admin.settings;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.OFF_BOT)
public class OffBot extends Processor {
    @Override
    public void run(Update update) {
        UpdateDispatcher.setIsOn(false);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, "Бот выключен");
        processToAdminMainPanel(chatId);
    }
}
