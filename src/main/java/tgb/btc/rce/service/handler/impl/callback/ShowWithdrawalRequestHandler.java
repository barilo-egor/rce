package tgb.btc.rce.service.handler.impl.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class ShowWithdrawalRequestHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final WithdrawalOfFundsService withdrawalOfFundsService;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    private final IWithdrawalRequestService withdrawalRequestService;

    public ShowWithdrawalRequestHandler(IResponseSender responseSender,
                                        WithdrawalOfFundsService withdrawalOfFundsService,
                                        ICallbackDataService callbackDataService,
                                        IKeyboardBuildService keyboardBuildService,
                                        IWithdrawalRequestService withdrawalRequestService) {
        this.responseSender = responseSender;
        this.withdrawalOfFundsService = withdrawalOfFundsService;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long requestId = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        responseSender.sendMessage(chatId, withdrawalOfFundsService.toString(withdrawalRequestService.findById(requestId)),
                keyboardBuildService.buildInline(List.of(InlineButton.builder()
                        .text("Скрыть")
                        .data(callbackDataService.buildData(CallbackQueryData.HIDE_WITHDRAWAL, requestId))
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build())));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SHOW_WITHDRAWAL_REQUEST;
    }
}
