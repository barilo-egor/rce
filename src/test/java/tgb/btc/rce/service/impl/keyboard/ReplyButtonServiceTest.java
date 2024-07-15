package tgb.btc.rce.service.impl.keyboard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReplyButtonServiceTest {

    private final IReplyButtonService replyButtonService = new ReplyButtonService();

    @NullSource
    @EmptySource
    @ParameterizedTest
    @DisplayName("Должен пробросить BaseException на пустой список команд/null.")
    void fromCommandsEmptyValues(List<Command> commands) {
        assertThrows(BaseException.class, () -> replyButtonService.fromCommands(commands));
    }

    @ParameterizedTest
    @MethodSource("getCommands")
    @DisplayName("Размер списка команд и кнопок должны совпадать")
    void fromCommandsSizeTest(List<Command> commands) {
        assertEquals(commands.size(), replyButtonService.fromCommands(commands).size());
    }

    static Stream<Arguments> getCommands() {
        return Stream.of(
                Arguments.of(List.of(Command.START)),
                Arguments.of(List.of(Command.DRAWS, Command.NEW_PAYMENT_TYPE_REQUISITE,Command.NEW_DEALS)),
                Arguments.of(List.of(Command.OPERATOR_PANEL, Command.ADMIN_PANEL, Command.TURN_PAYMENT_TYPES, Command.CAPTCHA,
                        Command.SLOT_REEL))
        );
    }

    @Test
    @DisplayName("Текст команд и в кнопках должны совпадать.")
    void fromCommands() {
        List<Command> commands = new ArrayList<>();
        commands.add(Command.START);
        commands.add(Command.OPERATOR_PANEL);
        commands.add(Command.NEW_DEALS);
        List<ReplyButton> replyButtons = replyButtonService.fromCommands(commands);
        assertAll(
                () -> assertEquals(Command.START.getText(), replyButtons.get(0).getText()),
                () -> assertEquals(Command.OPERATOR_PANEL.getText(), replyButtons.get(1).getText()),
                () -> assertEquals(Command.NEW_DEALS.getText(), replyButtons.get(2).getText())
        );
    }
}