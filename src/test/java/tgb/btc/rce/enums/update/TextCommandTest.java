package tgb.btc.rce.enums.update;

import org.junit.jupiter.api.Test;
import tgb.btc.library.constants.enums.bot.UserRole;

import static org.junit.jupiter.api.Assertions.*;

class TextCommandTest {

    @Test
    void hasAccess() {
        for (TextCommand textCommand : TextCommand.values()) {
            for (UserRole userRole : UserRole.values()) {
                assertEquals(textCommand.getRoles().contains(userRole), textCommand.hasAccess(userRole));
            }
        }
    }
}