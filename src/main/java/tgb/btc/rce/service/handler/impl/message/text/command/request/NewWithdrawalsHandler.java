package tgb.btc.rce.service.handler.impl.message.text.command.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class NewWithdrawalsHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IWithdrawalRequestService withdrawalRequestService;

    private final WithdrawalOfFundsService withdrawalOfFundsService;

    private final IKeyboardBuildService keyboardBuildService;

    private final ICallbackDataService callbackDataService;

    public NewWithdrawalsHandler(IResponseSender responseSender, IWithdrawalRequestService withdrawalRequestService,
                                 WithdrawalOfFundsService withdrawalOfFundsService,
                                 IKeyboardBuildService keyboardBuildService,
                                 ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.withdrawalRequestService = withdrawalRequestService;
        this.withdrawalOfFundsService = withdrawalOfFundsService;
        this.keyboardBuildService = keyboardBuildService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(Message message) {
        List<WithdrawalRequest> withdrawalRequests = withdrawalRequestService.getAllActive();
        Long chatId = message.getChatId();

        if (withdrawalRequests.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        withdrawalRequests.forEach(withdrawalRequest ->
                responseSender.sendMessage(chatId, withdrawalOfFundsService.toString(withdrawalRequest),
                        keyboardBuildService.buildInline(List.of(InlineButton.builder()
                                .text("Скрыть")
                                .data(callbackDataService.buildData(CallbackQueryData.HIDE_WITHDRAWAL, withdrawalRequest.getPid()))
                                .inlineType(InlineType.CALLBACK_DATA)
                                .build()))));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.NEW_WITHDRAWALS;
    }
}
