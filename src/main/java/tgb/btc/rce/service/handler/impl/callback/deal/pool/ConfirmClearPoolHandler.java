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
public class ConfirmClearPoolHandler implements ICallbackQueryHandler {

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public ConfirmClearPoolHandler(ICryptoWithdrawalService cryptoWithdrawalService, IResponseSender responseSender,
                                   ICallbackDataService callbackDataService) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        if (true) {
            return;
        }
        Long chatId = callbackQuery.getFrom().getId();
        Optional<Message> processMessage = responseSender.sendMessage(chatId, "Пул в процессе очищения, пожалуйста подождите");
        try {
            cryptoWithdrawalService.clearPool();
        } catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            return;
        }
        processMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        Integer poolMessageId = callbackDataService.getIntArgument(callbackQuery.getData(), 1);
        responseSender.deleteMessage(chatId, poolMessageId);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        log.debug("Пользователь {} очистил BTC пул.", chatId);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CONFIRM_CLEAR_POOL;
    }
}
