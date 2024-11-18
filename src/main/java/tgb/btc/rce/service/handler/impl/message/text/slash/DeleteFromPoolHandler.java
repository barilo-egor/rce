package tgb.btc.rce.service.handler.impl.message.text.slash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;
import tgb.btc.rce.service.processors.admin.requests.deal.BitcoinPool;

@Service
@Slf4j
public class DeleteFromPoolHandler implements ISlashCommandHandler {

    private final IResponseSender responseSender;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final BitcoinPool bitcoinPool;

    public DeleteFromPoolHandler(IResponseSender responseSender, ICryptoWithdrawalService cryptoWithdrawalService,
                                 BitcoinPool bitcoinPool) {
        this.responseSender = responseSender;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.bitcoinPool = bitcoinPool;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        Long poolId = Long.parseLong(message.getText().split(" ")[1]);
        responseSender.deleteMessage(message.getChatId(), message.getMessageId());
        try {
            cryptoWithdrawalService.deleteFromPool(poolId);
        } catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            return;
        }
        log.debug("Пользователь {} удалил сделку id={} из BTC пула.", chatId, poolId);
        bitcoinPool.processPool(chatId);
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.DELETE_FROM_POOL;
    }
}
