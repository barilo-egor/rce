package tgb.btc.lib.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.UpdateDispatcher;
import tgb.btc.lib.util.UpdateUtil;

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
