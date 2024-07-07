package tgb.btc.rce.service.processors.admin.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.util.UpdateUtil;

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
        switch (readUserService.getStepByChatId(chatId)) {
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
