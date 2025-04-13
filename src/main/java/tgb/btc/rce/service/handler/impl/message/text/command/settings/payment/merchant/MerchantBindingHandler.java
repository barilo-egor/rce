package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.merchant;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

public abstract class MerchantBindingHandler implements ITextCommandHandler {

    private final IBotMerchantService botMerchantService;

    protected MerchantBindingHandler(IBotMerchantService botMerchantService) {
        this.botMerchantService = botMerchantService;
    }

    @Override
    public void handle(Message message) {
        botMerchantService.sendRequestPaymentType(getMerchant(), message.getChatId());
    }

    public abstract Merchant getMerchant();
}
