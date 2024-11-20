package tgb.btc.rce.service.impl.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountsMenu implements IMenu {

    private IModule<ReferralType> referralModule;

    private IReplyButtonService replyButtonService;

    @Autowired
    public void setReplyButtonService(IReplyButtonService replyButtonService) {
        this.replyButtonService = replyButtonService;
    }

    @Autowired
    public void setReferralModule(IModule<ReferralType> referralModule) {
        this.referralModule = referralModule;
    }

    @Override
    public Menu getMenu() {
        return Menu.DISCOUNTS;
    }

    @Override
    public List<ReplyButton> build(UserRole userRole) {
        List<TextCommand> resultCommands = new ArrayList<>(getMenu().getTextCommands());
        if (!referralModule.isCurrent(ReferralType.STANDARD)) {
            resultCommands.remove(TextCommand.REFERRAL_PERCENT);
        }
        return replyButtonService.fromTextCommands(resultCommands);
    }

    @Override
    public boolean isOneTime() {
        return false;
    }
}
