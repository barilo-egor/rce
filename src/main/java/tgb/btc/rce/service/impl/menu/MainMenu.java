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
public class MainMenu implements IMenu {

    private IReplyButtonService replyButtonService;

    private IModule<ReferralType> referralModule;

    @Autowired
    public void setReferralModule(IModule<ReferralType> referralModule) {
        this.referralModule = referralModule;
    }

    @Autowired
    public void setReplyButtonService(IReplyButtonService replyButtonService) {
        this.replyButtonService = replyButtonService;
    }

    @Override
    public Menu getMenu() {
        return Menu.MAIN;
    }

    @Override
    public List<ReplyButton> build(UserRole userRole) {
        List<TextCommand> resultTextCommands = new ArrayList<>(getMenu().getTextCommands());
        if (!referralModule.isCurrent(ReferralType.STANDARD)) {
            resultTextCommands.remove(TextCommand.REFERRAL);
        }
        if (!UserRole.ADMIN.equals(userRole)) {
            resultTextCommands.remove(TextCommand.ADMIN_PANEL);
        }
        if (!UserRole.OPERATOR.equals(userRole)) {
            resultTextCommands.remove(TextCommand.OPERATOR_PANEL);
        }
        if (!UserRole.ADMIN.equals(userRole) && !UserRole.OPERATOR.equals(userRole)) {
            resultTextCommands.remove(TextCommand.WEB_ADMIN_PANEL);
        }
        if (!UserRole.OBSERVER.equals(userRole)) {
            resultTextCommands.remove(TextCommand.OBSERVER_PANEL);
        }
        return replyButtonService.fromTextCommands(resultTextCommands);
    }

    @Override
    public boolean isOneTime() {
        return false;
    }
}
