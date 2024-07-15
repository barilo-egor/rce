package tgb.btc.rce.service.impl.menu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.keyboard.IReplyButtonService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
class DrawsMenuTest {

    @Mock
    private IReplyButtonService replyButtonService;

    @InjectMocks
    private DrawsMenu drawsMenu;

    @Test
    void isOneTime() {
        assertFalse(drawsMenu.isOneTime());
    }

    @Test
    void getMenu() {
        assertEquals(Menu.DRAWS, drawsMenu.getMenu());
    }

    @Test
    void buildNoneTypeForUser() {

    }
}