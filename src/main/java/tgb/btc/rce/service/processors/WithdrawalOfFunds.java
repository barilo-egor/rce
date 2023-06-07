package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.WithdrawalRequestService;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.WITHDRAWAL_OF_FUNDS)
public class WithdrawalOfFunds extends Processor {
    private final WithdrawalOfFundsService withdrawalOfFundsService;
    private final WithdrawalRequestService withdrawalRequestService;

    @Autowired
    public WithdrawalOfFunds(WithdrawalOfFundsService withdrawalOfFundsService, WithdrawalRequestService withdrawalRequestService) {
        this.withdrawalOfFundsService = withdrawalOfFundsService;
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                if (withdrawalRequestService.getActiveByUserChatId(chatId) > 0) {
                    responseSender.sendMessage(chatId, "У вас уже есть активная заявка.",
                            KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .text("Удалить")
                                            .data(Command.DELETE_WITHDRAWAL_REQUEST.getText()
                                                    + BotStringConstants.CALLBACK_DATA_SPLITTER
                                                    + withdrawalRequestService.getPidByUserChatId(chatId))
                                            .build()
                            )));
                    return;
                }
                int minSum = BotVariablePropertiesUtil.getInt(BotVariableType.REFERRAL_MIN_SUM);
                if (userService.getReferralBalanceByChatId(chatId) < minSum) {
                    responseSender.sendMessage(chatId, "Минимальная сумма для вывода средств равна " + minSum + "₽");
                    return;
                }
                withdrawalOfFundsService.askForContact(chatId, UpdateUtil.getMessage(update).getMessageId());
                break;
            case 1:
                if (withdrawalOfFundsService.createRequest(update)) processToMainMenu(chatId);
                break;
        }
    }
}
