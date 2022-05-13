package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.WITHDRAWAL_OF_FUNDS)
public class WithdrawalOfFunds extends Processor {
    private final WithdrawalOfFundsService withdrawalOfFundsService;
    private final UserService userService;

    @Autowired
    public WithdrawalOfFunds(IResponseSender responseSender, WithdrawalOfFundsService withdrawalOfFundsService, UserService userService) {
        super(responseSender);
        this.withdrawalOfFundsService = withdrawalOfFundsService;
        this.userService = userService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        checkForCancel(update);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                if (withdrawalOfFundsService.isBalanceLessThanMinSum(chatId)) {
                    withdrawalOfFundsService.sendMinSumMessage(chatId);
                    return;
                }
                withdrawalOfFundsService.askForSum(UpdateUtil.getMessageId(update), chatId);
                break;
            case 1:
                withdrawalOfFundsService.createRequest(update);
                processToMainMenu(chatId);
                break;
        }
    }
}
