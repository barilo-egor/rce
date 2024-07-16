package tgb.btc.rce.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.impl.menu.BotSettingsMenu;
import tgb.btc.rce.service.impl.menu.DrawsMenu;
import tgb.btc.rce.service.impl.menu.MainMenu;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MainMenu mainMenu;

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

//    @Test
//    void buildMainUser() {
//        List<IMenu> menus = List.of(mainMenu);
//        when(mainMenu.build(any())).thenReturn(new ArrayList<>());
//        when(mainMenu.isOneTime()).thenReturn(false);
//        when(mainMenu.getMenu()).thenReturn(Menu.MAIN);
//        MenuService menuService = new MenuService(menus);
//        try (MockedStatic<KeyboardUtil> keyboardUtilMockedStatic = mockStatic(KeyboardUtil.class)) {
//            menuService.build(Menu.MAIN, UserRole.USER);
//            keyboardUtilMockedStatic.verify(()
//                    -> KeyboardUtil.buildReply(eq(Menu.MAIN.getNumberOfColumns()), anyList(), anyBoolean()));
//        }
//    }
}