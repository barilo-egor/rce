package tgb.btc.rce.service.processors.admin.hidden;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.admin.requests.deal.BitcoinPool;

@CommandProcessor(command = Command.DELETE_FROM_POOL)
@Slf4j
public class DeleteFromPool extends Processor {

    private BitcoinPool bitcoinPool;

    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Autowired
    public DeleteFromPool(BitcoinPool bitcoinPool, ICryptoWithdrawalService cryptoWithdrawalService) {
        this.bitcoinPool = bitcoinPool;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long poolId = Long.parseLong(updateService.getMessageText(update).split(" ")[1]);
        responseSender.deleteCallbackMessageIfExists(update);
        try {
            cryptoWithdrawalService.deleteFromPool(poolId);
        } catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            return;
        }
        log.debug("Пользователь {} удалил сделку id={} из BTC пула.", chatId, poolId);
        bitcoinPool.run(update);
    }
}
