package tgb.btc.rce.service.impl.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.keyboard.IReplyButtonService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainMenuTest {

    @Mock
    private IModule<ReferralType> referralModule;

    @Mock
    private IReplyButtonService replyButtonService;

    @InjectMocks
    private MainMenu mainMenu;

    @Test
    @DisplayName("Меню должно быть Menu.MAIN")
    void getMenu() {
        assertEquals(Menu.MAIN, mainMenu.getMenu());
    }

    @Test
    @DisplayName("Меню должно быть oneTime=false.")
    void isOneTime() {
        assertFalse(mainMenu.isOneTime());
    }

    @Test
    @DisplayName("Должен вернуть меню для USER.")
    void buildUser() {
        when(referralModule.isCurrent(ReferralType.STANDARD)).thenReturn(true);
        List<Command> expectedCommands = new ArrayList<>(Menu.MAIN.getCommands());
        expectedCommands.remove(Command.ADMIN_PANEL);
        expectedCommands.remove(Command.WEB_ADMIN_PANEL);
        expectedCommands.remove(Command.OPERATOR_PANEL);
        mainMenu.build(UserRole.USER);
        verify(replyButtonService).fromCommands(expectedCommands);
        when(referralModule.isCurrent(ReferralType.STANDARD)).thenReturn(false);
        expectedCommands.remove(Command.REFERRAL);
        mainMenu.build(UserRole.USER);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должен вернуть меню для ADMIN.")
    void buildAdmin() {
        when(referralModule.isCurrent(ReferralType.STANDARD)).thenReturn(true);
        List<Command> expectedCommands = new ArrayList<>(Menu.MAIN.getCommands());
        expectedCommands.remove(Command.OPERATOR_PANEL);
        mainMenu.build(UserRole.ADMIN);
        verify(replyButtonService).fromCommands(expectedCommands);
        when(referralModule.isCurrent(ReferralType.STANDARD)).thenReturn(false);
        expectedCommands.remove(Command.REFERRAL);
        mainMenu.build(UserRole.ADMIN);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должен вернуть меню для OPERATOR.")
    void buildOperator() {
        when(referralModule.isCurrent(ReferralType.STANDARD)).thenReturn(true);
        List<Command> expectedCommands = new ArrayList<>(Menu.MAIN.getCommands());
        expectedCommands.remove(Command.ADMIN_PANEL);
        expectedCommands.remove(Command.WEB_ADMIN_PANEL);
        mainMenu.build(UserRole.OPERATOR);
        verify(replyButtonService).fromCommands(expectedCommands);
        when(referralModule.isCurrent(ReferralType.STANDARD)).thenReturn(false);
        expectedCommands.remove(Command.REFERRAL);
        mainMenu.build(UserRole.OPERATOR);
        verify(replyButtonService).fromCommands(expectedCommands);
    }
}