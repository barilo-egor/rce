package tgb.btc.lib.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.UserInfoService;
import tgb.btc.lib.service.processors.support.MessagesService;
import tgb.btc.lib.util.NumberUtil;
import tgb.btc.lib.util.UpdateUtil;

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
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                messagesService.askForChatId(update, Command.USER_INFORMATION);
                break;
            case 1:
                userInfoService.sendUserInformation(chatId, NumberUtil.getInputLong(UpdateUtil.getMessageText(update)));
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
