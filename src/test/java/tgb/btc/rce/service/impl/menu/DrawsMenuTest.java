package tgb.btc.rce.service.impl.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.library.constants.enums.DiceType;
import tgb.btc.library.constants.enums.RPSType;
import tgb.btc.library.constants.enums.SlotReelType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.service.module.DiceModule;
import tgb.btc.library.service.module.RPSModule;
import tgb.btc.library.service.module.SlotReelModule;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.keyboard.IReplyButtonService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class DrawsMenuTest {

    @Mock
    private IReplyButtonService replyButtonService;

    @Mock
    private SlotReelModule slotReelModule;

    @Mock
    private DiceModule diceModule;

    @Mock
    private RPSModule rpsModule;

    @InjectMocks
    private DrawsMenu drawsMenu;

    @Test
    @DisplayName("Меню должно быть oneTime=false.")
    void isOneTime() {
        assertFalse(drawsMenu.isOneTime());
    }

    @Test
    @DisplayName("Меню должно быть Menu.DRAWS")
    void getMenu() {
        assertEquals(Menu.DRAWS, drawsMenu.getMenu());
    }

    @Test
    @DisplayName("Должен удалить кнопки игр для NONE и USER.")
    void buildNoneTypeForUser() {
        when(slotReelModule.getCurrent()).thenReturn(SlotReelType.NONE);
        when(diceModule.getCurrent()).thenReturn(DiceType.NONE);
        when(rpsModule.getCurrent()).thenReturn(RPSType.NONE);

        List<Command> expectedCommands = new ArrayList<>(Menu.DRAWS.getCommands());
        expectedCommands.removeIf(command ->
                Command.SLOT_REEL.equals(command) || Command.DICE.equals(command) || Command.RPS.equals(command));
        drawsMenu.build(UserRole.USER);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должен удалить кнопки игр для NONE и ADMIN.")
    void buildNoneTypeForAdmin() {
        when(slotReelModule.getCurrent()).thenReturn(SlotReelType.NONE);
        when(diceModule.getCurrent()).thenReturn(DiceType.NONE);
        when(rpsModule.getCurrent()).thenReturn(RPSType.NONE);

        List<Command> expectedCommands = new ArrayList<>(Menu.DRAWS.getCommands());
        expectedCommands.removeIf(command ->
                Command.SLOT_REEL.equals(command) || Command.DICE.equals(command) || Command.RPS.equals(command));
        drawsMenu.build(UserRole.ADMIN);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должен удалить кнопки игр для STANDARD_ADMIN и USER.")
    void buildStandardAdminTypeForUser() {
        when(slotReelModule.getCurrent()).thenReturn(SlotReelType.STANDARD_ADMIN);
        when(diceModule.getCurrent()).thenReturn(DiceType.STANDARD_ADMIN);
        when(rpsModule.getCurrent()).thenReturn(RPSType.STANDARD_ADMIN);

        List<Command> expectedCommands = new ArrayList<>(Menu.DRAWS.getCommands());
        expectedCommands.removeIf(command ->
                Command.SLOT_REEL.equals(command) || Command.DICE.equals(command) || Command.RPS.equals(command));
        drawsMenu.build(UserRole.USER);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должен оставить кнопки игр для STANDARD_ADMIN и ADMIN.")
    void buildStandardAdminTypeForAdmin() {
        when(slotReelModule.getCurrent()).thenReturn(SlotReelType.STANDARD_ADMIN);
        when(diceModule.getCurrent()).thenReturn(DiceType.STANDARD_ADMIN);
        when(rpsModule.getCurrent()).thenReturn(RPSType.STANDARD_ADMIN);

        List<Command> expectedCommands = new ArrayList<>(Menu.DRAWS.getCommands());
        drawsMenu.build(UserRole.ADMIN);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должен оставить кнопки игр для STANDARD и USER.")
    void buildStandardTypeForUser() {
        when(slotReelModule.getCurrent()).thenReturn(SlotReelType.STANDARD);
        when(diceModule.getCurrent()).thenReturn(DiceType.STANDARD);
        when(rpsModule.getCurrent()).thenReturn(RPSType.STANDARD);

        List<Command> expectedCommands = new ArrayList<>(Menu.DRAWS.getCommands());
        drawsMenu.build(UserRole.USER);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должен оставить кнопки игр для STANDARD и ADMIN.")
    void buildStandardTypeForAdmin() {
        when(slotReelModule.getCurrent()).thenReturn(SlotReelType.STANDARD);
        when(diceModule.getCurrent()).thenReturn(DiceType.STANDARD);
        when(rpsModule.getCurrent()).thenReturn(RPSType.STANDARD);

        List<Command> expectedCommands = new ArrayList<>(Menu.DRAWS.getCommands());
        drawsMenu.build(UserRole.ADMIN);
        verify(replyButtonService).fromCommands(expectedCommands);
    }
}