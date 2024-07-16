package tgb.btc.rce.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.impl.menu.BotSettingsMenu;
import tgb.btc.rce.service.impl.menu.DrawsMenu;
import tgb.btc.rce.service.impl.menu.MainMenu;
import tgb.btc.rce.service.impl.util.MenuService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private BotSettingsMenu botSettingsMenu;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @Captor
    private ArgumentCaptor<List<ReplyButton>> replyButtonCaptor;

    @Test
    @DisplayName("Должен разместить бины в EnumMap.")
    void init() {
        MenuService menuService = new MenuService(List.of(new MainMenu(), new DrawsMenu(), new BotSettingsMenu()), keyboardBuildService);
        Map<Menu, IMenu> actual = menuService.getMenuMap();
        assertAll(
                () -> assertEquals(3, actual.size()),
                () -> assertNotNull(actual.get(Menu.MAIN)),
                () -> assertTrue(actual.get(Menu.MAIN) instanceof MainMenu),
                () -> assertNotNull(actual.get(Menu.DRAWS)),
                () -> assertTrue(actual.get(Menu.DRAWS) instanceof DrawsMenu),
                () -> assertNotNull(actual.get(Menu.BOT_SETTINGS)),
                () -> assertTrue(actual.get(Menu.BOT_SETTINGS) instanceof BotSettingsMenu)
        );
    }

    @Test
    @DisplayName("Должен заменить на новый бин при повторном ключе")
    void initReplace() {
        MainMenu mainMenu1 = new MainMenu();
        MainMenu mainMenu2 = new MainMenu();
        MenuService menuService = new MenuService(List.of(mainMenu1, mainMenu2), keyboardBuildService);
        assertEquals(mainMenu2, menuService.getMenuMap().get(Menu.MAIN));
    }

    @Test
    @DisplayName("Должен создать кастом меню настроек бота для ADMIN.")
    void buildBotSettingsAdmin() {
        when(botSettingsMenu.getMenu()).thenReturn(Menu.BOT_SETTINGS);
        MenuService menuService = new MenuService(List.of(botSettingsMenu), keyboardBuildService);
        when(keyboardBuildService.buildReply(anyInt(), anyList(), anyBoolean())).thenReturn(new ReplyKeyboardMarkup());
        menuService.build(Menu.BOT_SETTINGS, UserRole.ADMIN);
        verify(keyboardBuildService).buildReply(eq(Menu.BOT_SETTINGS.getNumberOfColumns()), anyList(), anyBoolean());
    }

    @Test
    @DisplayName("Должен создать меню DISCOUNTS для ADMIN.")
    void buildDrawsUser() {
        MenuService menuService = new MenuService(List.of(), keyboardBuildService);
        when(keyboardBuildService.buildReply(anyInt(), anyList(), anyBoolean())).thenReturn(new ReplyKeyboardMarkup());
        menuService.build(Menu.DISCOUNTS, UserRole.ADMIN);
        verify(keyboardBuildService).buildReply(eq(Menu.DISCOUNTS.getNumberOfColumns()), replyButtonCaptor.capture(), eq(false));
        List<ReplyButton> actual = replyButtonCaptor.getValue();
        assertEquals(Menu.DISCOUNTS.getCommands().size(), actual.size());
        int i = 0;
        for (Command command : Menu.DISCOUNTS.getCommands()) {
            assertEquals(command.getText(), actual.get(i++).getText());
        }
    }
}