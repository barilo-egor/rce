package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.merchant;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.message.text.ISimpleTextHandler;

import java.util.Set;

public abstract class MerchantBindingHandler implements ISimpleTextHandler {

    private final IBotMerchantService botMerchantService;

    protected MerchantBindingHandler(IBotMerchantService botMerchantService) {
        this.botMerchantService = botMerchantService;
    }

    @Override
    public void handle(Message message) {
        botMerchantService.sendRequestPaymentType(getMerchant(), message.getChatId());
    }

    @Override
    public String getText() {
        return getMerchant().getBindingButtonText();
    }

    @Override
    public Set<UserRole> getRoles() {
        return UserRole.ADMIN_ACCESS;
    }

    public abstract Merchant getMerchant();
}
