package tgb.btc.rce.service.impl.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.keyboard.IReplyButtonService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BotSettingsMenuTest {

    @Mock
    private IReplyButtonService replyButtonService;

    @Mock
    private IUpdateDispatcher updateDispatcher;

    @InjectMocks
    private BotSettingsMenu botSettingsMenu;

    @Test
    @DisplayName("Меню должно быть oneTime=true.")
    void isOneTime() {
        assertTrue(botSettingsMenu.isOneTime());
    }

    @Test
    @DisplayName("Меню должно быть Menu.BOT_SETTINGS")
    void getMenu() {
        assertEquals(Menu.BOT_SETTINGS, botSettingsMenu.getMenu());
    }

    @Test
    @DisplayName("Должна быть кнопка \"Выкл.бота\"")
    void buildWithBotOn() {
        when(updateDispatcher.isOn()).thenReturn(true);

        List<Command> initialCommands = new ArrayList<>(Menu.BOT_SETTINGS.getCommands());
        List<Command> expectedCommands = new ArrayList<>(initialCommands);
        expectedCommands.removeIf(Command.ON_BOT::equals);

        botSettingsMenu.build(null);
        verify(replyButtonService).fromCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должна быть кнопка \"Вкл.бота\"")
    void buildWithBotOff() {
        when(updateDispatcher.isOn()).thenReturn(false);

        List<Command> initialCommands = new ArrayList<>(Menu.BOT_SETTINGS.getCommands());
        List<Command> expectedCommands = new ArrayList<>(initialCommands);
        expectedCommands.removeIf(Command.OFF_BOT::equals);

        botSettingsMenu.build(null);
        verify(replyButtonService).fromCommands(expectedCommands);
    }
}