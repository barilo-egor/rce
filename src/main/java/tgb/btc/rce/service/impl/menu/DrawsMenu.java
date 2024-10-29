package tgb.btc.rce.service.impl.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.DiceType;
import tgb.btc.library.constants.enums.RPSType;
import tgb.btc.library.constants.enums.SlotReelType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.IModule;
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

    private IModule<SlotReelType> slotReelModule;

    private IModule<DiceType> diceModule;

    private IModule<RPSType> rpsModule;

    @Autowired
    public void setSlotReelModule(IModule<SlotReelType> slotReelModule) {
        this.slotReelModule = slotReelModule;
    }

    @Autowired
    public void setDiceModule(IModule<DiceType> diceModule) {
        this.diceModule = diceModule;
    }

    @Autowired
    public void setRpsModule(IModule<RPSType> rpsModule) {
        this.rpsModule = rpsModule;
    }

    @Autowired
    public void setReplyButtonService(IReplyButtonService replyButtonService) {
        this.replyButtonService = replyButtonService;
    }

    @Override
    public Menu getMenu() {
        return Menu.DRAWS;
    }

    @Override
    public List<ReplyButton> build(UserRole userRole) {
        List<Command> resultCommands = new ArrayList<>(getMenu().getCommands());
        boolean isAdmin = userRole.equals(UserRole.ADMIN);
        SlotReelType currentSlotReelType = slotReelModule.getCurrent();
        if (SlotReelType.NONE.equals(currentSlotReelType) || (SlotReelType.STANDARD_ADMIN.equals(currentSlotReelType) && !isAdmin))
            resultCommands.remove(Command.SLOT_REEL);
        DiceType currentDiceType = diceModule.getCurrent();
        if (DiceType.NONE.equals(currentDiceType) || (DiceType.STANDARD_ADMIN.equals(currentDiceType) && !isAdmin))
            resultCommands.remove(Command.DICE);
        RPSType currentRPSType = rpsModule.getCurrent();
        if (RPSType.NONE.equals(currentRPSType) || (RPSType.STANDARD_ADMIN.equals(currentRPSType) && !isAdmin))
            resultCommands.remove(Command.RPS);
        return replyButtonService.fromCommands(resultCommands);
    }

    @Override
    public boolean isOneTime() {
        return false;
    }
}
