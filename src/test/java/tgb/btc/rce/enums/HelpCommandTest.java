package tgb.btc.rce.enums;

import org.junit.jupiter.api.Test;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.update.SlashCommand;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HelpCommandTest {

    @Test
    protected void shouldThrowException() {
        assertThrows(BaseException.class, () -> HelpCommand.getDescription(null));
    }

    @Test
    protected void shouldReturnEmptyString() {
        assertEquals("", HelpCommand.getDescription(SlashCommand.CHAT_ID));
    }

    @Test
    protected void shouldReturnDescription() {
        Map<HelpCommand, SlashCommand> slashCommands = Arrays.stream(HelpCommand.values())
                .collect(Collectors.toMap(helpCommand -> helpCommand, HelpCommand::getCommand));
        for (Map.Entry<HelpCommand, SlashCommand> entry : slashCommands.entrySet()) {
            assertEquals(entry.getKey().getDescription(), HelpCommand.getDescription(entry.getValue()));
        }
    }
}