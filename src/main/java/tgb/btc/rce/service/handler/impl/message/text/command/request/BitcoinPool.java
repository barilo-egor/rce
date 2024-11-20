package tgb.btc.rce.service.handler.impl.message.text.command.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.IBitcoinPoolService;

@Service
public class BitcoinPool implements ITextCommandHandler {

    private final IBitcoinPoolService bitcoinPoolService;

    public BitcoinPool(IBitcoinPoolService bitcoinPoolService) {
        this.bitcoinPoolService = bitcoinPoolService;
    }

    @Override
    public void handle(Message message) {
        bitcoinPoolService.process(message.getChatId());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BITCOIN_POOL;
    }
}
