package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class EvoPayBindingHandler implements ITextCommandHandler {

    private final IBotMerchantService botMerchantService;

    public EvoPayBindingHandler(IBotMerchantService botMerchantService) {
        this.botMerchantService = botMerchantService;
    }

    @Override
    public void handle(Message message) {
        botMerchantService.sendRequestPaymentType(Merchant.EVO_PAY, message.getChatId());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.EVO_PAY_BINDING;
    }
}
