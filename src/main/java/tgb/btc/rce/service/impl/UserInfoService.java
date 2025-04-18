package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.ILotteryWinService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IUserInfoService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

import java.util.List;
import java.util.Objects;

@Service
public class UserInfoService implements IUserInfoService {

    private IReadUserService readUserService;

    private IResponseSender responseSender;

    private IDealCountService dealCountService;

    private ILotteryWinService lotteryWinService;

    private IMessagePropertiesService messagePropertiesService;

    private IModule<ReferralType> referralModule;

    @Autowired
    public void setReferralModule(IModule<ReferralType> referralModule) {
        this.referralModule = referralModule;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setLotteryWinService(ILotteryWinService lotteryWinService) {
        this.lotteryWinService = lotteryWinService;
    }

    @Autowired
    public void setDealCountService(IDealCountService dealCountService) {
        this.dealCountService = dealCountService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void sendUserInformation(Long messageChatId, Long userChatId) {
        responseSender.sendMessage(messageChatId, getUserInformation(userChatId));
    }

    @Override
    public void sendUserInformation(Long messageChatId, Long userChatId, ReplyKeyboard replyKeyboard) {
        responseSender.sendMessage(messageChatId, getUserInformation(userChatId), replyKeyboard);
    }

    private String getUserInformation(Long chatId) {
        User user = readUserService.findByChatId(chatId);
        String userName = Objects.nonNull(user.getUsername()) ? user.getUsername() : "скрыт";
        Long dealsCount = dealCountService.getCountConfirmedByUserChatId(chatId);
        List<ReferralUser> referralUsers = readUserService.getUserReferralsByChatId(chatId);
        String role = user.getUserRole().getDisplayName();
        String isBanned = Boolean.TRUE.equals(user.getBanned()) ? "да" : "нет";
        Long lotteryWinCount = lotteryWinService.getLotteryWinCount(chatId);
        String fromChatId = Objects.nonNull(user.getFromChatId()) ? String.valueOf(user.getFromChatId()) : "отсутствует";
        String result;
        if (referralModule.isCurrent(ReferralType.STANDARD)) {
            int numberOfReferrals = referralUsers.size();
            int numberOfActiveReferrals = (int) referralUsers.stream()
                    .filter(usr -> dealCountService.getCountConfirmedByUserChatId(usr.getChatId()) > 0).count();
            String currentBalance = readUserService.getReferralBalanceByChatId(chatId).toString();
            result = String.format(messagePropertiesService.getMessage(PropertiesMessage.USER_INFORMATION_MAIN),
                    chatId, userName, dealsCount, numberOfReferrals,
                    numberOfActiveReferrals, currentBalance, isBanned, role,
                    lotteryWinCount, fromChatId);
        } else {
            result = String.format(messagePropertiesService.getMessage(PropertiesMessage.USER_INFORMATION_WITHOUT_REFERRAL_MAIN),
                    chatId, userName, dealsCount, isBanned, role,
                    lotteryWinCount, fromChatId);
        }
        return result;
    }
}
