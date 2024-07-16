package tgb.btc.rce.service.processors.admin.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.process.IBotMessageProcessService;


@CommandProcessor(command = Command.BOT_MESSAGES)
public class BotMessages extends Processor {

    private IBotMessageProcessService botMessageProcessService;

    @Autowired
    public void setBotMessagesService(IBotMessageProcessService botMessageProcessService) {
        this.botMessageProcessService = botMessageProcessService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                botMessageProcessService.askForType(chatId);
                break;
            case 1:
                botMessageProcessService.askForNewValue(update);
                break;
            case 2:
                botMessageProcessService.updateValue(update);
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
