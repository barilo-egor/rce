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

    @Autowired
    public WithdrawalOfFunds(IResponseSender responseSender, UserService userService,
                             WithdrawalOfFundsService withdrawalOfFundsService) {
        super(responseSender, userService);
        this.withdrawalOfFundsService = withdrawalOfFundsService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                withdrawalOfFundsService.askForContact(chatId, UpdateUtil.getMessage(update).getMessageId());
                break;
            case 1:
                if (withdrawalOfFundsService.createRequest(update)) processToMainMenu(chatId);
                break;
        }
    }
}
