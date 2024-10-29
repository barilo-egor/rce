package tgb.btc.rce.service.processors.admin.hidden;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.interfaces.service.bean.bot.*;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@Slf4j
@CommandProcessor(command = Command.DELETE_USER)
public class DeleteUser extends Processor {

    private IModifyDealService modifyDealService;

    private IUserDiscountService userDiscountService;

    private IUserDataService userDataService;

    private IPaymentReceiptService paymentReceiptService;

    private IWithdrawalRequestService withdrawalRequestService;

    private ILotteryWinService lotteryWinService;

    private IReferralUserService referralUserService;

    private ISpamBanService spamBanService;

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Autowired
    public void setUserDiscountService(IUserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Autowired
    public void setPaymentReceiptService(IPaymentReceiptService paymentReceiptService) {
        this.paymentReceiptService = paymentReceiptService;
    }

    @Autowired
    public void setWithdrawalRequestService(IWithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Autowired
    public void setLotteryWinService(ILotteryWinService lotteryWinService) {
        this.lotteryWinService = lotteryWinService;
    }

    @Autowired
    public void setReferralUserService(IReferralUserService referralUserService) {
        this.referralUserService = referralUserService;
    }

    @Autowired
    public void setSpamBanService(ISpamBanService spamBanService) {
        this.spamBanService = spamBanService;
    }

    @Override
    @Transactional
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        try {
            Long userChatId = Long.parseLong(updateService.getMessageText(update).split(" ")[1]);
            userDiscountService.deleteByUser_ChatId(userChatId);
            userDataService.deleteByUser_ChatId(userChatId);
            withdrawalRequestService.deleteByUser_ChatId(userChatId);
            lotteryWinService.deleteByUser_ChatId(userChatId);
            paymentReceiptService.getByDealsPids(userChatId);
            modifyDealService.deleteByUser_ChatId(userChatId);
            User user = readUserService.getByChatId(userChatId);
            spamBanService.deleteByUser_Pid(user.getPid());
            modifyUserService.delete(user);
            referralUserService.deleteAll(user.getReferralUsers());
            responseSender.sendMessage(chatId, "Пользователь " + userChatId + " удален.");
        } catch (Exception e) {
            log.error("Ошибки при удалении пользователя.", e);
            responseSender.sendMessage(chatId, "Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

}
