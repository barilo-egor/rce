package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.util.Optional;

@CommandProcessor(command = Command.CONFIRM_POOL_WITHDRAWAL)
@Slf4j
public class ConfirmBitcoinPoolWithdrawal extends Processor {

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    @Autowired
    public ConfirmBitcoinPoolWithdrawal(ICryptoWithdrawalService cryptoWithdrawalService) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Integer messageId = callbackQueryService.getSplitIntData(update, 1);
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
        responseSender.deleteCallbackMessageIfExists(update);
        log.debug("Пользователь {} завершил пул BTC.", chatId);
    }
}
