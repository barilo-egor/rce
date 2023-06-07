package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.processors.support.MessagesService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.SEND_MESSAGE_TO_USER)
public class SendMessageToUser extends Processor {

    private MessagesService messagesService;

    @Autowired
    public void setMessagesService(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                messagesService.askForChatId(update, Command.SEND_MESSAGE_TO_USER);
                break;
            case 1:
                if (messagesService.isUserExist(update))
                    messagesService.askForMessageText(update, Command.SEND_MESSAGE_TO_USER);
                break;
            case 2:
                messagesService.sendMessageToUser(update);
                processToAdminMainPanel(UpdateUtil.getChatId(update));
                break;
        }
    }
}
