package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.LotteryWinRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserInfoService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.NumberUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    public UserInformation(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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
