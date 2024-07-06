package tgb.btc.rce.service.impl.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.ON_BOT)
public class OnBot extends Processor {

    @Override
    public void run(Update update) {
        UpdateDispatcher.setIsOn(true);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, "Бот включен.");
        processToAdminMainPanel(chatId);
    }
}
