package tgb.btc.rce.service.impl.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotSettingsMenu implements IMenu {

    private IReplyButtonService replyButtonService;

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
        List<Command> commands = new ArrayList<>(Menu.BOT_SETTINGS.getCommands());
        commands.removeIf(command -> (UpdateDispatcher.isOn() && Command.ON_BOT.equals(command)
                || (!UpdateDispatcher.isOn() && Command.OFF_BOT.equals(command))));
        return replyButtonService.fromCommands(commands);
    }

    @Override
    public boolean isOneTime() {
        return true;
    }
}
