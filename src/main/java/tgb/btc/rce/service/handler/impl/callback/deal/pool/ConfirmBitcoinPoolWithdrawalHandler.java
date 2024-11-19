package tgb.btc.rce.service.handler.impl.callback.deal.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Optional;

@Service
@Slf4j
public class ConfirmBitcoinPoolWithdrawalHandler implements ICallbackQueryHandler {

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    public ConfirmBitcoinPoolWithdrawalHandler(ICryptoWithdrawalService cryptoWithdrawalService,
                                               ICallbackDataService callbackDataService, IResponseSender responseSender) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Integer messageId = callbackDataService.getIntArgument(callbackQuery.getData(), 1);
        Optional<Message> completeMessage = responseSender.sendMessage(chatId, "Выполняется завершение пула, пожалуйста подождите.");
        try {
            cryptoWithdrawalService.complete();
        }  catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            return;
        } finally {
            completeMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        }
        responseSender.deleteMessage(chatId, messageId);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        log.debug("Пользователь {} завершил пул BTC.", chatId);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CONFIRM_POOL_WITHDRAWAL;
    }
}
