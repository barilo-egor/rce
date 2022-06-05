package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.WithdrawalRequest;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.impl.WithdrawalRequestService;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.NEW_WITHDRAWALS)
public class NewWithdrawals extends Processor {

    private final WithdrawalRequestService withdrawalRequestService;
    private final WithdrawalOfFundsService withdrawalOfFundsService;

    @Autowired
    public NewWithdrawals(IResponseSender responseSender, UserService userService,
                          WithdrawalRequestService withdrawalRequestService,
                          WithdrawalOfFundsService withdrawalOfFundsService) {
        super(responseSender, userService);
        this.withdrawalRequestService = withdrawalRequestService;
        this.withdrawalOfFundsService = withdrawalOfFundsService;
    }

    @Override
    public void run(Update update) {
        List<WithdrawalRequest> withdrawalRequests = withdrawalRequestService.getAllActive();
        Long chatId = UpdateUtil.getChatId(update);

        if (withdrawalRequests.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        withdrawalRequests.forEach(withdrawalRequest ->
                responseSender.sendMessage(chatId, withdrawalOfFundsService.toString(withdrawalRequest),
                        KeyboardUtil.buildInline(List.of(InlineButton.builder()
                                .text("Скрыть")
                                .data(Command.HIDE_WITHDRAWAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                        + withdrawalRequest.getPid())
                                .inlineType(InlineType.CALLBACK_DATA)
                                .build()))));
    }
}
