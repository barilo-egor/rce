package tgb.btc.rce.service.handler.impl.callback.request.withdrawal;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DeleteWithdrawalRequestHandler implements ICallbackQueryHandler {

    private final IWithdrawalRequestService withdrawalRequestService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public DeleteWithdrawalRequestHandler(IWithdrawalRequestService withdrawalRequestService,
                                          IResponseSender responseSender, ICallbackDataService callbackDataService) {
        this.withdrawalRequestService = withdrawalRequestService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        withdrawalRequestService.delete(withdrawalRequestService.findById(
                callbackDataService.getLongArgument(callbackQuery.getData(), 1)
        ));
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Заявка на вывод средств удалена.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETE_WITHDRAWAL_REQUEST;
    }
}
