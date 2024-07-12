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
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;

@Service
public class UserInfoService {

    private IReadUserService readUserService;

    private IResponseSender responseSender;

    private ISpamBanService spamBanService;

    private IDealCountService dealCountService;

    private ILotteryWinService lotteryWinService;

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

    public void sendUserInformation(Long messageChatId, Long userChatId) {
        responseSender.sendMessage(messageChatId, getUserInformation(userChatId), null, "HTML");
    }

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
            result = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.USER_INFORMATION_MAIN),
                    chatId, userName, dealsCount, numberOfReferrals,
                    numberOfActiveReferrals, currentBalance, isBanned, role,
                    lotteryWinCount, fromChatId);
        } else {
            result = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.USER_INFORMATION_WITHOUT_REFERRAL_MAIN),
                    chatId, userName, dealsCount, isBanned, role,
                    lotteryWinCount, fromChatId);
        }
        return result;
    }

    public void sendSpamBannedUser(Long messageChatId, Long spamBanPid) {
        sendUserInformation(messageChatId, spamBanService.getUserChatIdByPid(spamBanPid),
                            KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .text("Оставить в бане")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.KEEP_SPAM_BAN, spamBanPid.toString()
                                            ))
                                            .build(),
                                    InlineButton.builder()
                                            .text("Разблокировать")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.SPAM_UNBAN, spamBanPid.toString()
                                            ))
                                            .build()
                            )));
    }
}
