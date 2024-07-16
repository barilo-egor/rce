package tgb.btc.rce.service.processors.admin.requests.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.NEW_WITHDRAWALS)
public class NewWithdrawals extends Processor {

    private IWithdrawalRequestService withdrawalRequestService;

    private WithdrawalOfFundsService withdrawalOfFundsService;

    @Autowired
    public void setWithdrawalRequestService(IWithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Autowired
    public void setWithdrawalOfFundsService(WithdrawalOfFundsService withdrawalOfFundsService) {
        this.withdrawalOfFundsService = withdrawalOfFundsService;
    }

    @Override
    public void run(Update update) {
        List<WithdrawalRequest> withdrawalRequests = withdrawalRequestService.getAllActive();
        Long chatId = updateService.getChatId(update);

        if (withdrawalRequests.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        withdrawalRequests.forEach(withdrawalRequest ->
                responseSender.sendMessage(chatId, withdrawalOfFundsService.toString(withdrawalRequest),
                        keyboardBuildService.buildInline(List.of(InlineButton.builder()
                                .text("Скрыть")
                                .data(Command.HIDE_WITHDRAWAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                        + withdrawalRequest.getPid())
                                .inlineType(InlineType.CALLBACK_DATA)
                                .build()))));
    }
}
