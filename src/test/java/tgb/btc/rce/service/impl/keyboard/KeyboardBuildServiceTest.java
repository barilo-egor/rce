package tgb.btc.rce.service.impl.keyboard;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.impl.util.CallbackDataService;
import tgb.btc.rce.service.impl.util.TextTextCommandService;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardBuildServiceTest {

    private final CallbackDataService callbackDataService = new CallbackDataService();

    private final ButtonsDesignPropertiesReader buttonsDesignPropertiesReader = new ButtonsDesignPropertiesReader();

    private final KeyboardBuildService keyboardBuildService = new KeyboardBuildService(callbackDataService);

    private final TextTextCommandService textCommandService = new TextTextCommandService(buttonsDesignPropertiesReader);

    @Test
    void buildReplyButtonsLessThanColumns() {
        for (int i = 0; i < 3; i++) {
            int randomButtonsSize = RandomUtils.secure().randomInt(1, 10);
            int randomColumnsSize = RandomUtils.secure().randomInt(11, 99);
            List<ReplyButton> replyButtons = new ArrayList<>();
            for (int j = 0; j < randomButtonsSize; j++) {
                replyButtons.add(ReplyButton.builder()
                        .text(RandomStringUtils.secure().nextAlphanumeric(5))
                        .build());
            }
            ReplyKeyboardMarkup keyboardMarkup = keyboardBuildService.buildReply(randomColumnsSize, false, false, replyButtons);
            assertEquals(1, keyboardMarkup.getKeyboard().size(),
                    "Для " + randomButtonsSize + " кнопок и " + randomColumnsSize + " колонок должна быть 1 строка.");
        }
    }

    @Test
    void buildReplyButtonsMoreThanColumns() {
        for (int i = 0; i < 3; i++) {
            int randomButtonsSize = RandomUtils.secure().randomInt(11, 99);
            int randomColumnsSize = RandomUtils.secure().randomInt(1, 9);
            List<ReplyButton> replyButtons = new ArrayList<>();
            for (int j = 0; j < randomButtonsSize; j++) {
                replyButtons.add(ReplyButton.builder()
                        .text(StringUtils.EMPTY)
                        .build());
            }
            ReplyKeyboardMarkup keyboardMarkup = keyboardBuildService.buildReply(randomColumnsSize, false, false, replyButtons);
            int numberOfRows = (int) Math.ceil((double) randomButtonsSize / randomColumnsSize);
            assertEquals(numberOfRows, keyboardMarkup.getKeyboard().size(),
                    "Для " + randomButtonsSize + " кнопок и " + randomColumnsSize
                            + " колонок должно быть " + numberOfRows + " строк.");
        }
    }

    @Test
    void buildReplyButtonsCountTest() {
        List<ReplyButton> replyButtons = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            replyButtons.add(ReplyButton.builder().text(StringUtils.EMPTY).build());
        }
        ReplyKeyboardMarkup keyboardMarkup = keyboardBuildService.buildReply(3, true, true, replyButtons);
        assertEquals(replyButtons.size(), (Integer) keyboardMarkup.getKeyboard().stream().mapToInt(ArrayList::size).sum());
    }

    @Test
    void buildReplyWithNegativeOrZeroNumbersOfColumns() {
        List<ReplyButton> replyButtons = new ArrayList<>();
        assertAll(
                () -> assertThrows(BaseException.class,
                        () -> keyboardBuildService.buildReply(0, false, false, replyButtons)),
                () -> assertThrows(BaseException.class,
                        () -> keyboardBuildService.buildReply(-100, false, false, replyButtons))
        );
    }

    @Test
    void buildReplyButtonFieldsTest() {
        List<ReplyButton> replyButtons = new ArrayList<>();
        replyButtons.add(ReplyButton.builder()
                .text(SlashCommand.START.getText())
                .isRequestContact(false)
                .isRequestLocation(true)
                .build());
        replyButtons.add(ReplyButton.builder()
                .text(textCommandService.getText(TextCommand.BACK))
                .isRequestContact(true)
                .isRequestLocation(false)
                .build());
        replyButtons.add(ReplyButton.builder()
                .text(textCommandService.getText(TextCommand.REPORTS))
                .build());
        ReplyKeyboardMarkup keyboardMarkup = keyboardBuildService.buildReply(3, true, true, replyButtons);
        KeyboardRow keyboardButtons = keyboardMarkup.getKeyboard().get(0);
        assertAll(
                () -> assertNull(keyboardButtons.get(0).getRequestContact()),
                () -> assertTrue(keyboardButtons.get(0).getRequestLocation()),
                () -> assertEquals(SlashCommand.START.getText(), keyboardButtons.get(0).getText()),
                () -> assertTrue(keyboardButtons.get(1).getRequestContact()),
                () -> assertNull(keyboardButtons.get(1).getRequestLocation()),
                () -> assertEquals(textCommandService.getText(TextCommand.BACK), keyboardButtons.get(1).getText()),
                () -> assertNull(keyboardButtons.get(2).getRequestContact()),
                () -> assertNull(keyboardButtons.get(2).getRequestLocation()),
                () -> assertEquals(textCommandService.getText(TextCommand.REPORTS), keyboardButtons.get(2).getText())
        );
    }

    @Test
    void buildReplyKeyboardFieldsTest() {
        List<ReplyButton> replyButtons = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            replyButtons.add(ReplyButton.builder()
                    .text(StringUtils.EMPTY)
                    .build());
        }
        ReplyKeyboardMarkup keyboardMarkup1 = keyboardBuildService.buildReply(3, true, true, replyButtons);
        assertAll(
                () -> assertTrue(keyboardMarkup1.getOneTimeKeyboard()),
                () -> assertTrue(keyboardMarkup1.getResizeKeyboard())
        );
        ReplyKeyboardMarkup keyboardMarkup2 = keyboardBuildService.buildReply(3, false, false, replyButtons);
        assertAll(
                () -> assertFalse(keyboardMarkup2.getOneTimeKeyboard()),
                () -> assertFalse(keyboardMarkup2.getResizeKeyboard())
        );
    }

    @Test
    void buildReplyEmptyButtons() {
        assertAll(
                () -> assertThrows(BaseException.class, this::invokeBuildReplyWithNull),
                () -> assertThrows(BaseException.class, this::invokeBuildReplyWithEmptyList)
        );
    }

    private void invokeBuildReplyWithNull() {
        keyboardBuildService.buildReply(1, true, true, null);
    }

    private void invokeBuildReplyWithEmptyList() {
        keyboardBuildService.buildReply(1, true, true, new ArrayList<>());
    }

    @Test
    void buildReplyOnlyButtons() {
        List<ReplyButton> replyButtons = new ArrayList<>();
        int numberOfButtons = 3;
        for (int i = 0; i < numberOfButtons; i++) {
            replyButtons.add(ReplyButton.builder().text("qwe").build());
        }
        ReplyKeyboardMarkup keyboardMarkup = keyboardBuildService.buildReply(replyButtons);
        assertAll(
                () -> assertEquals(numberOfButtons, keyboardMarkup.getKeyboard().size()),
                () -> assertFalse(keyboardMarkup.getOneTimeKeyboard()),
                () -> assertTrue(keyboardMarkup.getResizeKeyboard())
        );
    }

    @Test
    void buildReplyButtonsAndNumberOfColumns() {
        List<ReplyButton> replyButtons = new ArrayList<>();
        int numberOfButtons = 3;
        for (int i = 0; i < numberOfButtons; i++) {
            replyButtons.add(ReplyButton.builder().text("qwe").build());
        }
        int numberOfColumns = 2;
        ReplyKeyboardMarkup keyboardMarkup = keyboardBuildService.buildReply(numberOfColumns, replyButtons);
        assertAll(
                () -> assertEquals(numberOfColumns, keyboardMarkup.getKeyboard().size()),
                () -> assertFalse(keyboardMarkup.getOneTimeKeyboard()),
                () -> assertTrue(keyboardMarkup.getResizeKeyboard())
        );
    }

    @Test
    void buildReplyButtonsAndNumberOfColumnsAndOneTime() {
        List<ReplyButton> replyButtons = new ArrayList<>();
        int numberOfButtons = 3;
        for (int i = 0; i < numberOfButtons; i++) {
            replyButtons.add(ReplyButton.builder().text("qwe").build());
        }
        int numberOfColumns = 2;
        ReplyKeyboardMarkup keyboardMarkup = keyboardBuildService.buildReply(numberOfColumns, replyButtons, true);
        assertAll(
                () -> assertEquals(numberOfColumns, keyboardMarkup.getKeyboard().size()),
                () -> assertTrue(keyboardMarkup.getOneTimeKeyboard()),
                () -> assertTrue(keyboardMarkup.getResizeKeyboard())
        );
    }

    @Test
    void buildInlineRowsOneColumn() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        int numberOfButtons = 10;
        for (int i = 0; i < numberOfButtons; i++) {
            inlineButtons.add(InlineButton.builder().text("qwe").build());
        }
        assertEquals(numberOfButtons, keyboardBuildService.buildInlineRows(inlineButtons, 1).size());
    }

    @Test
    void buildInlineRowsLastRowFull() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            inlineButtons.add(InlineButton.builder().text("qwe").build());
        }
        assertEquals(2, keyboardBuildService.buildInlineRows(inlineButtons, 5).size());
    }

    @Test
    void buildInlineRowsLastRowNotFull() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            inlineButtons.add(InlineButton.builder().text("qwe").build());
        }
        assertEquals(2, keyboardBuildService.buildInlineRows(inlineButtons, 5).size());
    }

    @Test
    void buildInlineRowsNegativeColumns() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        assertAll(
                () -> assertThrows(BaseException.class, () -> keyboardBuildService.buildInlineRows(inlineButtons, 0)),
                () -> assertThrows(BaseException.class, () -> keyboardBuildService.buildInlineRows(inlineButtons, -100))
        );
    }

    @Test
    void buildInlineRowsOneFullRow() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            inlineButtons.add(InlineButton.builder().text("qwe").build());
        }
        assertEquals(5, keyboardBuildService.buildInlineRows(inlineButtons, 5).get(0).size());
    }

    @Test
    void buildInlineRowsOneNotFullRow() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            inlineButtons.add(InlineButton.builder().text("qwe").build());
        }
        assertEquals(3, keyboardBuildService.buildInlineRows(inlineButtons, 5).get(0).size());
    }

    @Test
    void buildInlineRowsParseTest() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        inlineButtons.add(InlineButton.builder()
                .text(SlashCommand.START.getText())
                .inlineType(InlineType.URL)
                .data(SlashCommand.START.name())
                .build());
        inlineButtons.add(InlineButton.builder()
                .text(textCommandService.getText(TextCommand.BACK))
                .inlineType(InlineType.CALLBACK_DATA)
                .data(TextCommand.BACK.name())
                .build());
        inlineButtons.add(InlineButton.builder()
                .text(textCommandService.getText(TextCommand.REPORTS))
                .inlineType(InlineType.SWITCH_INLINE_QUERY)
                .data(TextCommand.REPORTS.name())
                .build());
        inlineButtons.add(InlineButton.builder()
                .text(SlashCommand.START.getText())
                .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                .data(SlashCommand.START.name())
                .build());
        inlineButtons.add(InlineButton.builder()
                .text(textCommandService.getText(TextCommand.BACK))
                .inlineType(InlineType.WEB_APP)
                .data(TextCommand.BACK.name())
                .build());
        List<List<InlineKeyboardButton>> keyboard = keyboardBuildService.buildInlineRows(inlineButtons, 5);
        List<InlineKeyboardButton> buttons = keyboard.get(0);
        assertAll(
                () -> assertEquals(SlashCommand.START.getText(), buttons.get(0).getText()),
                () -> assertEquals(SlashCommand.START.name(), buttons.get(0).getUrl()),
                () -> assertEquals(textCommandService.getText(TextCommand.BACK), buttons.get(1).getText()),
                () -> assertEquals(TextCommand.BACK.name(), buttons.get(1).getCallbackData()),
                () -> assertEquals(textCommandService.getText(TextCommand.REPORTS), buttons.get(2).getText()),
                () -> assertEquals(TextCommand.REPORTS.name(), buttons.get(2).getSwitchInlineQuery()),
                () -> assertEquals(SlashCommand.START.getText(), buttons.get(3).getText()),
                () -> assertEquals(SlashCommand.START.name(), buttons.get(3).getSwitchInlineQueryCurrentChat()),
                () -> assertEquals(textCommandService.getText(TextCommand.BACK), buttons.get(4).getText()),
                () -> assertEquals(TextCommand.BACK.name(), buttons.get(4).getWebApp().getUrl())
        );
    }


    @Test
    void buildInlineRowsEmptyButtons() {
        assertAll(
                () -> assertThrows(BaseException.class, this::invokeBuildInlineRowsWithNull),
                () -> assertThrows(BaseException.class, this::invokeBuildInlineRowsWithEmptyList)
        );
    }

    private void invokeBuildInlineRowsWithNull() {
        keyboardBuildService.buildInlineRows(new ArrayList<>(), 2);
    }

    private void invokeBuildInlineRowsWithEmptyList() {
        keyboardBuildService.buildInlineRows(null, 5);
    }

    @Test
    void buildInlineSingleLastOneButton() {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder().text("qwe").build());
        InlineButton lastButton = InlineButton.builder().text("last").build();
        InlineKeyboardMarkup keyboardMarkup = keyboardBuildService.buildInlineSingleLast(buttons, 1, lastButton);
        assertAll(
                () -> assertEquals(2, keyboardMarkup.getKeyboard().size()),
                () -> assertEquals(1, keyboardMarkup.getKeyboard().get(1).size()),
                () -> assertEquals("last", keyboardMarkup.getKeyboard().get(1).get(0).getText())
        );
    }

    @Test
    void buildInlineSingleLastFewButtons() {
        List<InlineButton> buttons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            buttons.add(InlineButton.builder()
                    .text("qwe")
                    .build());
        }
        InlineButton lastButton = InlineButton.builder().text("last").build();
        InlineKeyboardMarkup keyboardMarkup = keyboardBuildService.buildInlineSingleLast(buttons, 3, lastButton);
        assertAll(
                () -> assertEquals(5, keyboardMarkup.getKeyboard().size()),
                () -> assertEquals(1, keyboardMarkup.getKeyboard().get(4).size()),
                () -> assertEquals("last", keyboardMarkup.getKeyboard().get(4).get(0).getText())
        );
    }

    @Test
    void buildInlineByRows() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        assertEquals(rows, keyboardBuildService.buildInlineByRows(rows).getKeyboard());
    }

    @Test
    void buildInlineWithColumnNumber() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        inlineButtons.add(InlineButton.builder().text("qwe").build());
        assertNotNull(keyboardBuildService.buildInline(inlineButtons, 1).getKeyboard());
    }

    @Test
    void buildInlineOnlyButtons() {
        List<InlineButton> inlineButtons = new ArrayList<>();
        inlineButtons.add(InlineButton.builder().text("qwe").build());
        assertNotNull(keyboardBuildService.buildInline(inlineButtons).getKeyboard());
    }

    @Test
    void buildContacts() {
        List<Contact> contacts = new ArrayList<>();
        int numberOfContacts = 3;
        for (int i = 0; i < numberOfContacts; i++) {
            contacts.add(Contact.builder().label("label" + i).url("url" + i).build());
        }
        InlineKeyboardMarkup keyboardMarkup = keyboardBuildService.buildContacts(contacts);
        List<List<InlineKeyboardButton>> rows = keyboardMarkup.getKeyboard();
        assertAll(
                () -> assertEquals(numberOfContacts, keyboardMarkup.getKeyboard().size()),
                () -> assertEquals(contacts.get(0).getLabel(), rows.get(0).get(0).getText()),
                () -> assertEquals(contacts.get(0).getUrl(), rows.get(0).get(0).getUrl()),
                () -> assertEquals(contacts.get(1).getLabel(), rows.get(1).get(0).getText()),
                () -> assertEquals(contacts.get(1).getUrl(), rows.get(1).get(0).getUrl()),
                () -> assertEquals(contacts.get(2).getLabel(), rows.get(2).get(0).getText()),
                () -> assertEquals(contacts.get(2).getUrl(), rows.get(2).get(0).getUrl())
        );
    }

    @Test
    void getLink() {
        String text = "text";
        String data = "url";
        InlineKeyboardButton inlineKeyboardButton = keyboardBuildService.getLink(text, data).getKeyboard().get(0).get(0);
        assertAll(
                () -> assertEquals(text, inlineKeyboardButton.getText()),
                () -> assertEquals(data, inlineKeyboardButton.getUrl())
        );
    }
}