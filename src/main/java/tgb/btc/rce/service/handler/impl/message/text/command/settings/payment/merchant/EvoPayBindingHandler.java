package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.merchant;

import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IBotMerchantService;

@Service
public class EvoPayBindingHandler extends MerchantBindingHandler {

    protected EvoPayBindingHandler(IBotMerchantService botMerchantService) {
        super(botMerchantService);
    }

    @Override
    public Merchant getMerchant() {
        return Merchant.EVO_PAY;
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.EVO_PAY_BINDING;
    }
}
