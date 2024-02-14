package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.repository.bot.LotteryWinRepository;
import tgb.btc.library.repository.bot.SpamBanRepository;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.repository.bot.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;

@Service
public class UserInfoService {

    private UserRepository userRepository;

    private IResponseSender responseSender;

    private SpamBanRepository spamBanRepository;

    private DealRepository dealRepository;

    private LotteryWinRepository lotteryWinRepository;

    @Autowired
    public void setLotteryWinRepository(LotteryWinRepository lotteryWinRepository) {
        this.lotteryWinRepository = lotteryWinRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setSpamBanRepository(SpamBanRepository spamBanRepository) {
        this.spamBanRepository = spamBanRepository;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    public void sendUserInformation(Long messageChatId, Long userChatId) {
        responseSender.sendMessage(messageChatId, getUserInformation(userChatId), null, "HTML");
    }

    public void sendUserInformation(Long messageChatId, Long userChatId, ReplyKeyboard replyKeyboard) {
        responseSender.sendMessage(messageChatId, getUserInformation(userChatId), replyKeyboard, "HTML");
    }

    private String getUserInformation(Long chatId) {
        User user = userRepository.findByChatId(chatId);
        String userName = Objects.nonNull(user.getUsername()) ? user.getUsername() : "скрыт";
        Long dealsCount = dealRepository.getCountPassedByUserChatId(chatId);
        List<ReferralUser> referralUsers = userRepository.getUserReferralsByChatId(chatId);
        String isAdmin = user.getAdmin() ? "да" : "нет";
        String isBanned = user.getBanned() ? "да" : "нет";
        Long lotteryWinCount = lotteryWinRepository.getLotteryWinCount(chatId);
        String fromChatId = Objects.nonNull(user.getFromChatId()) ? String.valueOf(user.getFromChatId()) : "отсутствует";
        String result = null;
        if (ReferralType.STANDARD.isCurrent()) {
            int numberOfReferrals = referralUsers.size();
            int numberOfActiveReferrals = (int) referralUsers.stream()
                    .filter(usr -> dealRepository.getCountPassedByUserChatId(usr.getChatId()) > 0).count();
            String currentBalance = userRepository.getReferralBalanceByChatId(chatId).toString();
            result = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.USER_INFORMATION_MAIN),
                    chatId, userName, dealsCount, numberOfReferrals,
                    numberOfActiveReferrals, currentBalance, isBanned, isAdmin,
                    lotteryWinCount, fromChatId);
        } else {
            result = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.USER_INFORMATION_WITHOUT_REFERRAL_MAIN),
                    chatId, userName, dealsCount, isBanned, isAdmin,
                    lotteryWinCount, fromChatId);
        }
        return result;
    }

    public void sendSpamBannedUser(Long messageChatId, Long spamBanPid) {
        sendUserInformation(messageChatId, spamBanRepository.getUserChatIdByPid(spamBanPid),
                            KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .text("Оставить в бане")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.KEEP_SPAM_BAN.name(), spamBanPid.toString()
                                            ))
                                            .build(),
                                    InlineButton.builder()
                                            .text("Разблокировать")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.SPAM_UNBAN.name(), spamBanPid.toString()
                                            ))
                                            .build()
                            )));
    }
}
