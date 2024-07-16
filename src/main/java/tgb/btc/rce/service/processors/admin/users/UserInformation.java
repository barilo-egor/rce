package tgb.btc.rce.service.processors.admin.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserInfoService;
import tgb.btc.rce.service.processors.support.MessagesService;


@Slf4j
@CommandProcessor(command = Command.USER_INFORMATION)
public class UserInformation extends Processor {

    private UserInfoService userInfoService;

    private MessagesService messagesService;

    @Autowired
    public void setMessagesService(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Autowired
    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                messagesService.askForChatId(update, Command.USER_INFORMATION);
                break;
            case 1:
                userInfoService.sendUserInformation(chatId, Long.parseLong(updateService.getMessageText(update)));
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
