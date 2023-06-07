package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.lib.bean.ReferralUser;
import tgb.btc.lib.bean.User;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.PropertiesMessage;
import tgb.btc.lib.enums.ReferralType;
import tgb.btc.lib.repository.DealRepository;
import tgb.btc.lib.repository.LotteryWinRepository;
import tgb.btc.lib.repository.SpamBanRepository;
import tgb.btc.lib.repository.UserRepository;
import tgb.btc.lib.service.IResponseSender;
import tgb.btc.lib.util.CallbackQueryUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.MessagePropertiesUtil;
import tgb.btc.lib.vo.InlineButton;

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
        if (ReferralType.STANDARD.equals(UserService.REFERRAL_TYPE)) {
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
                                                    Command.KEEP_SPAM_BAN.getText(), spamBanPid.toString()
                                            ))
                                            .build(),
                                    InlineButton.builder()
                                            .text("Разблокировать")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.SPAM_UNBAN.getText(), spamBanPid.toString()
                                            ))
                                            .build()
                            )));
    }
}
