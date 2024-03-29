package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.repository.bot.*;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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

    private final SpamBanRepository spamBanRepository;

    @Autowired
    public DeleteUser(UserRepository userRepository, DealRepository dealRepository,
                      UserDiscountRepository userDiscountRepository, UserDataRepository userDataRepository,
                      PaymentReceiptRepository paymentReceiptRepository,
                      WithdrawalRequestRepository withdrawalRequestRepository, LotteryWinRepository lotteryWinRepository,
                      ReferralUserRepository referralUserRepository, SpamBanRepository spamBanRepository) {
        this.userRepository = userRepository;
        this.dealRepository = dealRepository;
        this.userDiscountRepository = userDiscountRepository;
        this.userDataRepository = userDataRepository;
        this.paymentReceiptRepository = paymentReceiptRepository;
        this.withdrawalRequestRepository = withdrawalRequestRepository;
        this.lotteryWinRepository = lotteryWinRepository;
        this.referralUserRepository = referralUserRepository;
        this.spamBanRepository = spamBanRepository;
    }

    @Override
    @Transactional
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            Long userChatId = Long.parseLong(UpdateUtil.getMessageText(update).split(" ")[1]);
            userDiscountRepository.deleteByUser_ChatId(userChatId);
            userDataRepository.deleteByUser_ChatId(userChatId);
            withdrawalRequestRepository.deleteByUser_ChatId(userChatId);
            lotteryWinRepository.deleteByUser_ChatId(userChatId);
            paymentReceiptRepository.getByDealsPids(userChatId);
            dealRepository.deleteByUser_ChatId(userChatId);
            User user = userRepository.getByChatId(userChatId);
            spamBanRepository.deleteByUser_Pid(user.getPid());
            userRepository.delete(user);
            referralUserRepository.deleteAll(user.getReferralUsers());
            responseSender.sendMessage(chatId, "Пользователь " + userChatId + " удален.");
        } catch (Exception e) {
            log.error("Ошибки при удалении пользователя.", e);
            responseSender.sendMessage(chatId, "Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

}
