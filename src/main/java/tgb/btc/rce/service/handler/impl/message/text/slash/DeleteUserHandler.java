package tgb.btc.rce.service.handler.impl.message.text.slash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.interfaces.service.bean.bot.*;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;

@Service
@Slf4j
public class DeleteUserHandler implements ISlashCommandHandler {

    private final IModifyDealService modifyDealService;

    private final IUserDiscountService userDiscountService;

    private final IUserDataService userDataService;

    private final IPaymentReceiptService paymentReceiptService;

    private final IWithdrawalRequestService withdrawalRequestService;

    private final ILotteryWinService lotteryWinService;

    private final IReferralUserService referralUserService;

    private final ISpamBanService spamBanService;

    private final IReadUserService readUserService;

    private final IModifyUserService modifyUserService;

    private final IResponseSender responseSender;

    public DeleteUserHandler(IModifyDealService modifyDealService, IUserDiscountService userDiscountService,
                             IUserDataService userDataService, IPaymentReceiptService paymentReceiptService,
                             IWithdrawalRequestService withdrawalRequestService, ILotteryWinService lotteryWinService,
                             IReferralUserService referralUserService, ISpamBanService spamBanService,
                             IReadUserService readUserService, IModifyUserService modifyUserService,
                             IResponseSender responseSender) {
        this.modifyDealService = modifyDealService;
        this.userDiscountService = userDiscountService;
        this.userDataService = userDataService;
        this.paymentReceiptService = paymentReceiptService;
        this.withdrawalRequestService = withdrawalRequestService;
        this.lotteryWinService = lotteryWinService;
        this.referralUserService = referralUserService;
        this.spamBanService = spamBanService;
        this.readUserService = readUserService;
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        try {
            Long userChatId = Long.parseLong(message.getText().split(" ")[1]);
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

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.DELETE_USER;
    }
}
