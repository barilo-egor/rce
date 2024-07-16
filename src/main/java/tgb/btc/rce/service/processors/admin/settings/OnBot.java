package tgb.btc.rce.service.processors.admin.settings;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.util.UpdateDispatcher;


@CommandProcessor(command = Command.ON_BOT)
public class OnBot extends Processor {

    @Override
    public void run(Update update) {
        UpdateDispatcher.setIsOn(true);
        Long chatId = updateService.getChatId(update);
        responseSender.sendMessage(chatId, "Бот включен.");
        processToAdminMainPanel(chatId);
    }
}
