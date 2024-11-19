package tgb.btc.rce.service.impl.keyboard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.impl.UpdateService;
import tgb.btc.rce.service.impl.util.CommandService;
import tgb.btc.rce.service.keyboard.IReplyButtonService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReplyButtonServiceTest {

    private final IReplyButtonService replyButtonService = new ReplyButtonService();

    private final ButtonsDesignPropertiesReader buttonsDesignPropertiesReader = new ButtonsDesignPropertiesReader();

    private final CommandService commandService = new CommandService(new UpdateService(), buttonsDesignPropertiesReader);

    @NullSource
    @EmptySource
    @ParameterizedTest
    @DisplayName("Должен пробросить BaseException на пустой список команд/null.")
    void fromCommandsEmptyValues(List<Command> commands) {
        assertThrows(BaseException.class, () -> replyButtonService.fromCommands(commands));
    }
// TODO
//    @ParameterizedTest
//    @MethodSource("getCommands")
//    @DisplayName("Размер списка команд и кнопок должны совпадать")
//    void fromCommandsSizeTest(List<Command> commands) {
//        assertEquals(commands.size(), replyButtonService.fromCommands(commands).size());
//    }

    // TODO
//    @Test
//    @DisplayName("Текст команд и в кнопках должны совпадать.")
//    void fromCommands() {
//        List<Command> commands = new ArrayList<>();
//        commands.add(Command.START);
//        commands.add(Command.OPERATOR_PANEL);
//        commands.add(Command.NEW_DEALS);
//        List<ReplyButton> replyButtons = replyButtonService.fromCommands(commands);
//        assertAll(
//                () -> assertEquals(commandService.getText(Command.START), replyButtons.get(0).getText()),
//                () -> assertEquals(commandService.getText(Command.OPERATOR_PANEL), replyButtons.get(1).getText()),
//                () -> assertEquals(commandService.getText(Command.NEW_DEALS), replyButtons.get(2).getText())
//        );
//    }
}