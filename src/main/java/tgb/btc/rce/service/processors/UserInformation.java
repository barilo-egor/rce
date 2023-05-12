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

    private final MessagesService messagesService;
    private final DealService dealService;
    private final DealRepository dealRepository;

    private final LotteryWinRepository lotteryWinRepository;

    @Autowired
    public UserInformation(IResponseSender responseSender, UserService userService, MessagesService messagesService, DealService dealService, DealRepository dealRepository, LotteryWinRepository lotteryWinRepository) {
        super(responseSender, userService);
        this.messagesService = messagesService;
        this.dealService = dealService;
        this.dealRepository = dealRepository;
        this.lotteryWinRepository = lotteryWinRepository;
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
                sendUserInfo(update);
                processToAdminMainPanel(chatId);
                break;
        }
    }

    private void sendUserInfo(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long inputChatId = NumberUtil.getInputLong(UpdateUtil.getMessageText(update));
        User user = userService.findByChatId(inputChatId);
        String userName = Objects.nonNull(user.getUsername()) ? user.getUsername() : "скрыт";
        Long dealsCount = dealService.getCountPassedByUserChatId(chatId);
        List<ReferralUser> referralUsers = userService.getUserReferralsByChatId(chatId);
        int numberOfReferrals = referralUsers.size();
        int numberOfActiveReferrals = (int) referralUsers.stream()
                .filter(usr -> dealRepository.getCountPassedByUserChatId(usr.getChatId()) > 0).count();
        String currentBalance = userService.getReferralBalanceByChatId(chatId).toString();
        String isAdmin = user.getAdmin() ? "да" : "нет";
        String isBanned = user.getBanned() ? "да" : "нет";
        Long lotteryWinCount = lotteryWinRepository.getLotteryWinCount(inputChatId);
        String fromChatId = Objects.nonNull(user.getFromChatId()) ? String.valueOf(user.getFromChatId()) : "отсутствует";
        String resultMessage = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.USER_INFORMATION_MAIN),
                                             inputChatId, userName, dealsCount, numberOfReferrals,
                                             numberOfActiveReferrals, currentBalance, isBanned, isAdmin,
                                             lotteryWinCount, fromChatId);

        responseSender.sendMessage(chatId, resultMessage, "HTML");
    }

}
