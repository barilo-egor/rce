package tgb.btc.rce.service.impl.menu;

import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentTypesBindingMenu implements IMenu {
    @Override
    public Menu getMenu() {
        return Menu.PAYMENT_TYPES_BINDING;
    }

    @Override
    public List<ReplyButton> build(UserRole userRole) {
        List<ReplyButton> buttons = new ArrayList<>();
        for (Merchant merchant : Merchant.values()) {
            if (Merchant.NONE.equals(merchant)) {
                continue;
            }
            buttons.add(ReplyButton.builder().text(merchant.getBindingButtonText()).build());
        }
        buttons.add(ReplyButton.builder().text(TextCommand.RETURN.getText()).build());
        return buttons;
    }

    @Override
    public boolean isOneTime() {
        return false;
    }
}
