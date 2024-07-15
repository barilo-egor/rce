package tgb.btc.rce.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.impl.menu.BotSettingsMenu;
import tgb.btc.rce.service.impl.menu.DrawsMenu;
import tgb.btc.rce.service.impl.menu.MainMenu;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MenuServiceTest {

    @Mock
    private Map<Menu, IMenu> menuMap;

    @Mock
    private MainMenu mainMenu;

    @InjectMocks
    private MenuService menuService;

    @Test
    void init() {
        MenuService menuService = new MenuService(List.of(new MainMenu(), new DrawsMenu(), new BotSettingsMenu()));
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
}