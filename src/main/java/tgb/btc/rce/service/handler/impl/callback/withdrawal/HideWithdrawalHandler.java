package tgb.btc.rce.service.handler.impl.callback.withdrawal;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class HideWithdrawalHandler implements ICallbackQueryHandler {

    private final IWithdrawalRequestService withdrawalRequestService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public HideWithdrawalHandler(IWithdrawalRequestService withdrawalRequestService, IResponseSender responseSender,
                                 ICallbackDataService callbackDataService) {
        this.withdrawalRequestService = withdrawalRequestService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long requestPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        withdrawalRequestService.updateIsActiveByPid(false, requestPid);
        responseSender.deleteMessage(callbackQuery.getFrom().getId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.HIDE_WITHDRAWAL;
    }
}
