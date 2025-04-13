package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.merchant;

import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IBotMerchantService;

@Service
public class HoneyMoneyBindingHandler extends MerchantBindingHandler {

    protected HoneyMoneyBindingHandler(IBotMerchantService botMerchantService) {
        super(botMerchantService);
    }

    @Override
    public Merchant getMerchant() {
        return Merchant.HONEY_MONEY;
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.HONEY_MONEY_BINDING;
    }
}
