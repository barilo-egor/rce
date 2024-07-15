package tgb.btc.rce.service.impl.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.DiceType;
import tgb.btc.library.constants.enums.RPS;
import tgb.btc.library.constants.enums.SlotReelType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class DrawsMenu implements IMenu {

    private IReplyButtonService replyButtonService;

    @Autowired
    public void setReplyButtonService(IReplyButtonService replyButtonService) {
        this.replyButtonService = replyButtonService;
    }


    @Override
    public Menu getMenu() {
        return Menu.DRAWS;
    }

    @Override
    @Cacheable("drawsMenuCache")
    public List<ReplyButton> build(UserRole userRole) {
        List<Command> commands = new ArrayList<>(Menu.DRAWS.getCommands());
        boolean isAdmin = userRole.equals(UserRole.ADMIN);
        if (SlotReelType.NONE.isCurrent() || (SlotReelType.STANDARD_ADMIN.isCurrent() && !isAdmin))
            commands.remove(Command.SLOT_REEL);
        if (DiceType.NONE.isCurrent() || (DiceType.STANDARD_ADMIN.isCurrent() && !isAdmin))
            commands.remove(Command.DICE);
        if (RPS.NONE.isCurrent() || (RPS.STANDARD_ADMIN.isCurrent() && !isAdmin))
            commands.remove(Command.RPS);
        return replyButtonService.fromCommands(commands);
    }

    @Override
    public boolean isOneTime() {
        return false;
    }
}
