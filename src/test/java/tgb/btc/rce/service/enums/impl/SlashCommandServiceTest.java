package tgb.btc.rce.service.enums.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.rce.enums.update.SlashCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SlashCommandServiceTest {

    @Spy
    private SlashCommandService slashCommandService;

    @Test
    @DisplayName("Должен вернуть null, если передан null.")
    void fromMessageTextReturnNullIfNull() {
        assertNull(slashCommandService.fromMessageText(null));
    }

    @Test
    @DisplayName("Должен вернуть null, если передана пустая строка.")
    void fromMessageTextReturnNullIfEmpty() {
        assertNull(slashCommandService.fromMessageText(""));
    }

    @Test
    @DisplayName("Должен вернуть null, если передана несуществующая команда без аргументов.")
    void fromMessageTextReturnNullIfNotValidCommandWithoutArguments() {
        String text = "/qwe";
        assertNull(slashCommandService.fromMessageText(text));
    }

    @Test
    @DisplayName("Должен вернуть null, если передана несуществующая команда с аргументом.")
    void fromMessageTextReturnNullIfNotValidCommandWithArguments() {
        String text = "/qwe 123";
        assertNull(slashCommandService.fromMessageText(text));
    }

    @Test
    @DisplayName("Должен вернуть команду, если передана существующая команда без аргументов.")
    void fromMessageTextWithoutArgument() {
        String text = "/start";
        assertEquals(SlashCommand.START, slashCommandService.fromMessageText(text));
        text = "/chatid";
        assertEquals(SlashCommand.CHAT_ID, slashCommandService.fromMessageText(text));
    }

    @Test
    @DisplayName("Должен вернуть команду, если передана существующая команда с аргументом.")
    void fromMessageTextWithArgument() {
        String text = "/chatid 123";
        assertEquals(SlashCommand.CHAT_ID, slashCommandService.fromMessageText(text));
        text = "/makeuser 456";
        assertEquals(SlashCommand.MAKE_USER, slashCommandService.fromMessageText(text));
    }
}