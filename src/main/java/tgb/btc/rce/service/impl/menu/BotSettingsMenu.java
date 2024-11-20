package tgb.btc.rce.service.impl.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.service.util.IUpdateDispatcher;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

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
        List<TextCommand> resultCommands = new ArrayList<>(getMenu().getTextCommands());
        resultCommands.removeIf(command ->
                (updateDispatcher.isOn() && TextCommand.ON_BOT.equals(command)) || (!updateDispatcher.isOn() && TextCommand.OFF_BOT.equals(command)));
        return replyButtonService.fromTextCommands(resultCommands);
    }

    @Override
    public boolean isOneTime() {
        return true;
    }
}
