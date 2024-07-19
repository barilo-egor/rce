package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.interfaces.service.bean.bot.ILotteryWinService;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.IUserInfoService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;

@Service
public class UserInfoService implements IUserInfoService {

    private IReadUserService readUserService;

    private IResponseSender responseSender;

    private ISpamBanService spamBanService;

    private IDealCountService dealCountService;

    private ILotteryWinService lotteryWinService;

    private IKeyboardBuildService keyboardBuildService;

    private ICallbackQueryService callbackQueryService;

    private IMessagePropertiesService messagePropertiesService;

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setCallbackQueryService(ICallbackQueryService callbackQueryService) {
        this.callbackQueryService = callbackQueryService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setLotteryWinService(ILotteryWinService lotteryWinService) {
        this.lotteryWinService = lotteryWinService;
    }

    @Autowired
    public void setSpamBanService(ISpamBanService spamBanService) {
        this.spamBanService = spamBanService;
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
        responseSender.sendMessage(messageChatId, getUserInformation(userChatId), null, "HTML");
    }

    @Override
    public void sendUserInformation(Long messageChatId, Long userChatId, ReplyKeyboard replyKeyboard) {
        responseSender.sendMessage(messageChatId, getUserInformation(userChatId), replyKeyboard, "HTML");
    }

    private String getUserInformation(Long chatId) {
        User user = readUserService.findByChatId(chatId);
        String userName = Objects.nonNull(user.getUsername()) ? user.getUsername() : "скрыт";
        Long dealsCount = dealCountService.getCountPassedByUserChatId(chatId);
        List<ReferralUser> referralUsers = readUserService.getUserReferralsByChatId(chatId);
        String role = user.getUserRole().getDisplayName();
        String isBanned = user.getBanned() ? "да" : "нет";
        Long lotteryWinCount = lotteryWinService.getLotteryWinCount(chatId);
        String fromChatId = Objects.nonNull(user.getFromChatId()) ? String.valueOf(user.getFromChatId()) : "отсутствует";
        String result = null;
        if (ReferralType.STANDARD.isCurrent()) {
            int numberOfReferrals = referralUsers.size();
            int numberOfActiveReferrals = (int) referralUsers.stream()
                    .filter(usr -> dealCountService.getCountPassedByUserChatId(usr.getChatId()) > 0).count();
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

    @Override
    public void sendSpamBannedUser(Long messageChatId, Long spamBanPid) {
        sendUserInformation(messageChatId, spamBanService.getUserChatIdByPid(spamBanPid),
                            keyboardBuildService.buildInline(List.of(
                                    InlineButton.builder()
                                            .text(Command.KEEP_SPAM_BAN.getText())
                                            .data(callbackQueryService.buildCallbackData(
                                                    Command.KEEP_SPAM_BAN, spamBanPid.toString()
                                            ))
                                            .build(),
                                    InlineButton.builder()
                                            .text(Command.SPAM_UNBAN.getText())
                                            .data(callbackQueryService.buildCallbackData(
                                                    Command.SPAM_UNBAN, spamBanPid.toString()
                                            ))
                                            .build()
                            )));
    }
}
