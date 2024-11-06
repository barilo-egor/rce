package tgb.btc.rce.service.processors.admin.requests.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.util.Optional;

@CommandProcessor(command = Command.CONFIRM_CLEAR_POOL)
public class ConfirmClearPool extends Processor {

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    @Autowired
    public ConfirmClearPool(ICryptoWithdrawalService cryptoWithdrawalService) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Optional<Message> processMessage = responseSender.sendMessage(chatId, "Пул в процессе очищения, пожалуйста подождите");
        try {
            cryptoWithdrawalService.clearPool();
        } catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            return;
        }
        processMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        Integer poolMessageId = callbackQueryService.getSplitIntData(update, 1);
        responseSender.deleteMessage(chatId, poolMessageId);
        responseSender.deleteCallbackMessageIfExists(update);
        responseSender.sendMessage(chatId, "Пул BTC очищен.");
    }
}
