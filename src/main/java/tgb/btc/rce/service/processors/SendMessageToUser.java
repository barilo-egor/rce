package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SEND_MESSAGE_TO_USER)
public class SendMessageToUser extends Processor {

    private final MessagesService messagesService;

    @Autowired
    public SendMessageToUser(IResponseSender responseSender, UserService userService, MessagesService messagesService) {
        super(responseSender, userService);
        this.messagesService = messagesService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        checkForCancel(update);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                messagesService.askForChatId(update);
                break;
            case 1:
                messagesService.isUserExist(update);
                messagesService.askForMessageText(update);
                break;
            case 2:
                messagesService.sendMessageToUser(update);
                processToAdminMainPanel(UpdateUtil.getChatId(update));
                break;
        }
    }
}
