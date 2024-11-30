package tgb.btc.rce.service.impl.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IBotSwitch;
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
    private IBotSwitch botSwitch;

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
        when(botSwitch.isOn()).thenReturn(true);

        List<TextCommand> expectedCommands = new ArrayList<>(Menu.BOT_SETTINGS.getTextCommands());
        expectedCommands.removeIf(TextCommand.ON_BOT::equals);

        botSettingsMenu.build(null);
        verify(replyButtonService).fromTextCommands(expectedCommands);
    }

    @Test
    @DisplayName("Должна быть кнопка \"Вкл.бота\"")
    void buildWithBotOff() {
        when(botSwitch.isOn()).thenReturn(false);

        List<TextCommand> expectedCommands = new ArrayList<>(Menu.BOT_SETTINGS.getTextCommands());
        expectedCommands.removeIf(TextCommand.OFF_BOT::equals);

        botSettingsMenu.build(null);
        verify(replyButtonService).fromTextCommands(expectedCommands);
    }
}