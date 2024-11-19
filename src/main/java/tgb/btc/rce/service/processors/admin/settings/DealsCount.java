package tgb.btc.rce.service.processors.admin.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.properties.FunctionsPropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.MessagesService;


@CommandProcessor(command = Command.DEALS_COUNT)
public class DealsCount extends Processor {

    private MessagesService messagesService;

    private FunctionsPropertiesReader functionsPropertiesReader;

    @Autowired
    public void setFunctionsPropertiesReader(FunctionsPropertiesReader functionsPropertiesReader) {
        this.functionsPropertiesReader = functionsPropertiesReader;
    }

    @Autowired
    public void setMessagesService(MessagesService messagesService) {
        this.messagesService = messagesService;
    }


    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                messagesService.askForDealsCount(update, Command.DEALS_COUNT);
                break;
            case 1:
                functionsPropertiesReader.setProperty("allowed.deals.count", updateService.getMessageText(update));
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
