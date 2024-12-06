package tgb.btc.rce.enums.update;

import org.junit.jupiter.api.Test;
import tgb.btc.library.constants.enums.bot.UserRole;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlashCommandTest {

    @Test
    protected void hasAccess() {
        for (SlashCommand slashCommand : SlashCommand.values()) {
            for (UserRole userRole : UserRole.values()) {
                assertEquals(slashCommand.getRoles().contains(userRole), slashCommand.hasAccess(userRole));
            }
        }
    }

}