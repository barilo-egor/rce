package tgb.btc.rce.service.impl;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.impl.util.CallbackQueryService;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CallbackQueryServiceTest {

    private final CallbackQueryService callbackQueryService = new CallbackQueryService();


    @Test
    void getMessageId() {
        for (int i = 1; i <= 3 ; i++) {
            Update update = new Update();
            CallbackQuery callbackQuery = new CallbackQuery();
            Message message = new Message();
            int randomMessageId = RandomUtils.nextInt(100000, 999999);
            message.setMessageId(randomMessageId);
            callbackQuery.setMessage(message);
            update.setCallbackQuery(callbackQuery);
            assertEquals(randomMessageId, callbackQueryService.messageId(update));
        }
    }

    @Test
    void getMessageIdThrows() {
        Update update = new Update();
        assertThrows(BaseException.class, () -> callbackQueryService.messageId(update));
    }

    @Test
    void buildCallbackDataWithCommand() {
        String[] variables = new String[] {"123", "qwe", "1qw"};
        assertEquals(Command.START.getText()
                        .concat(BotStringConstants.CALLBACK_DATA_SPLITTER)
                        .concat(String.join(BotStringConstants.CALLBACK_DATA_SPLITTER, variables)),
                callbackQueryService.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithCommandAndVariablesDifferentTypes() {
        Object[] variables = new Object[] {"123", 123, 1245L, 12.25};
        assertEquals(Command.START.getText()
                        .concat(BotStringConstants.CALLBACK_DATA_SPLITTER)
                        .concat(Arrays.stream(variables).map(Object::toString)
                                .collect(Collectors.joining(BotStringConstants.CALLBACK_DATA_SPLITTER))),
                callbackQueryService.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithNullValue() {
        Object[] variables = new Object[] {"123", null, 1245L, 12.25};
        assertThrows(BaseException.class, () -> callbackQueryService.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithEmptyStringValue() {
        Object[] variables = new Object[] {"123", 123, 1245L, ""};
        assertThrows(BaseException.class, () -> callbackQueryService.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithBlankStringValue() {
        Object[] variables = new Object[] {"123", 123, 1245L, "       "};
        assertThrows(BaseException.class, () -> callbackQueryService.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithEmptyVariables() {
        assertThrows(BaseException.class, () -> callbackQueryService.buildCallbackData(Command.START, new Object[]{}));
    }

    @Test
    void isBackReturnTrue() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(Command.BACK.name());
        update.setCallbackQuery(callbackQuery);
        assertTrue(callbackQueryService.isBack(update));
    }

    @Test
    void isBackReturnFalse() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(Command.START.name());
        update.setCallbackQuery(callbackQuery);
        assertFalse(callbackQueryService.isBack(update));
        callbackQuery.setData(null);
        assertFalse(callbackQueryService.isBack(update));
        callbackQuery.setData("qwe");
        assertFalse(callbackQueryService.isBack(update));
        callbackQuery.setData("");
        assertFalse(callbackQueryService.isBack(update));
    }

    @Test
    void getSplitLongData() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        long l = 1000L;
        callbackQuery.setData(callbackQueryService.buildCallbackData(Command.START, new Object[]{l}));
        update.setCallbackQuery(callbackQuery);
        assertEquals(l, callbackQueryService.getSplitLongData(update, 1));
        l = 0;
        callbackQuery.setData(callbackQueryService.buildCallbackData(Command.BOT_OFFED, new Object[]{"123", l}));
        assertEquals(l, callbackQueryService.getSplitLongData(update, 2));
    }

    @Test
    void getSplitBooleanDataReturnTrue() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(callbackQueryService.buildCallbackData(Command.START, new String[]{"true"}));
        update.setCallbackQuery(callbackQuery);
        assertTrue(callbackQueryService.getSplitBooleanData(update, 1));
        callbackQuery.setData(callbackQueryService.buildCallbackData(Command.START, new Object[]{123, 123L, "qwe", "true"}));
        assertTrue(callbackQueryService.getSplitBooleanData(update, 4));
    }

    @Test
    void getSplitBooleanDataReturnFalse() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(callbackQueryService.buildCallbackData(Command.START, new String[]{"false"}));
        update.setCallbackQuery(callbackQuery);
        assertFalse(callbackQueryService.getSplitBooleanData(update, 1));
    }

    @Test
    void getSplitDataByUpdate() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(callbackQueryService.buildCallbackData(Command.START, new Object[]{123, "qwerty", false}));
        update.setCallbackQuery(callbackQuery);
        assertAll(
                () -> assertEquals("123", callbackQueryService.getSplitData(update, 1)),
                () -> assertEquals("qwerty", callbackQueryService.getSplitData(update, 2)),
                () -> assertEquals("false", callbackQueryService.getSplitData(update, 3))
        );
    }

    @Test
    void getSplitDataByCallbackQuery() {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(callbackQueryService.buildCallbackData(Command.START, new Object[]{123, "qwerty", false}));
        assertAll(
                () -> assertEquals("123", callbackQueryService.getSplitData(callbackQuery, 1)),
                () -> assertEquals("qwerty", callbackQueryService.getSplitData(callbackQuery, 2)),
                () -> assertEquals("false", callbackQueryService.getSplitData(callbackQuery, 3))
        );
    }

}