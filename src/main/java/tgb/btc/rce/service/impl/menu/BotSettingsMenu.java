package tgb.btc.rce.service.impl.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.service.util.IUpdateDispatcher;
import tgb.btc.rce.vo.ReplyButton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BotSettingsMenu implements IMenu {

    private IReplyButtonService replyButtonService;

    private IUpdateDispatcher updateDispatcher;

    @Autowired
    public void setUpdateDispatcher(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Autowired
    public void setReplyButtonService(IReplyButtonService replyButtonService) {
        this.replyButtonService = replyButtonService;
    }

    @Override
    public Menu getMenu() {
        return Menu.BOT_SETTINGS;
    }

    @Override
    public List<ReplyButton> build(UserRole userRole) {
        Set<Command> resultCommands = new HashSet<>(getMenu().getCommands());
        resultCommands.removeIf(command ->
                (updateDispatcher.isOn() && Command.ON_BOT.equals(command)) || (!updateDispatcher.isOn() && Command.OFF_BOT.equals(command)));
        return replyButtonService.fromCommands(resultCommands);
    }

    @Override
    public boolean isOneTime() {
        return true;
    }
}
