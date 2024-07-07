package tgb.btc.rce.service.processors.admin.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DEALS_COUNT)
public class DealsCount extends Processor {

    private MessagesService messagesService;

    @Autowired
    public void setMessagesService(MessagesService messagesService) {
        this.messagesService = messagesService;
    }


    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                messagesService.askForDealsCount(update, Command.DEALS_COUNT);
                break;
            case 1:
                PropertiesPath.FUNCTIONS_PROPERTIES.setProperty("allowed.deals.count", UpdateUtil.getMessageText(update));
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
