package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.BasePersist;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.*;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@CommandProcessor(command = Command.DELETE_USER)
public class DeleteUser extends Processor {

    private final UserRepository userRepository;

    private final DealRepository dealRepository;

    private final UserDiscountRepository userDiscountRepository;

    private final UserDataRepository userDataRepository;

    private final PaymentReceiptRepository paymentReceiptRepository;

    private final WithdrawalRequestRepository withdrawalRequestRepository;

    private final LotteryWinRepository lotteryWinRepository;

    private final ReferralUserRepository referralUserRepository;

    @Autowired
    public DeleteUser(IResponseSender responseSender, UserService userService, UserRepository userRepository,
                      DealRepository dealRepository, UserDiscountRepository userDiscountRepository,
                      UserDataRepository userDataRepository, PaymentReceiptRepository paymentReceiptRepository,
                      WithdrawalRequestRepository withdrawalRequestRepository, LotteryWinRepository lotteryWinRepository,
                      ReferralUserRepository referralUserRepository) {
        super(responseSender, userService);
        this.userRepository = userRepository;
        this.dealRepository = dealRepository;
        this.userDiscountRepository = userDiscountRepository;
        this.userDataRepository = userDataRepository;
        this.paymentReceiptRepository = paymentReceiptRepository;
        this.withdrawalRequestRepository = withdrawalRequestRepository;
        this.lotteryWinRepository = lotteryWinRepository;
        this.referralUserRepository = referralUserRepository;
    }

    @Override
    @Transactional
    public void run(Update update) {
        try {
            Long chatId = Long.parseLong(UpdateUtil.getMessageText(update).split(" ")[1]);
            userDiscountRepository.deleteByUserChatId(chatId);
            userDataRepository.deleteByUserChatId(chatId);
            withdrawalRequestRepository.deleteByUserChatId(chatId);
            lotteryWinRepository.deleteByUserChatId(chatId);
            paymentReceiptRepository.getByDealsPids(chatId);
            dealRepository.deleteByUserChatId(chatId);
            User user = userRepository.getByChatId(chatId);
            referralUserRepository.deleteAll(user.getReferralUsers());
            userRepository.delete(user);
            responseSender.sendMessage(chatId, "Пользователь " + chatId + " удален.");
        } catch (Exception e) {
            log.error("Ошибки при удалении пользователя.", e);
            responseSender.sendMessage(UpdateUtil.getChatId(update), "Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

}
