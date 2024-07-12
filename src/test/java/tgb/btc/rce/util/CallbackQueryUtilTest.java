package tgb.btc.rce.util;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CallbackQueryUtilTest {

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
            assertEquals(randomMessageId, CallbackQueryUtil.messageId(update));
        }
    }

    @Test
    void getMessageIdThrows() {
        Update update = new Update();
        assertThrows(BaseException.class, () -> CallbackQueryUtil.messageId(update));
    }

    @Test
    void buildCallbackDataWithCommand() {
        String[] variables = new String[] {"123", "qwe", "1qw"};
        assertEquals(Command.START.getText()
                .concat(BotStringConstants.CALLBACK_DATA_SPLITTER)
                .concat(String.join(BotStringConstants.CALLBACK_DATA_SPLITTER, variables)),
                CallbackQueryUtil.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithCommandAndVariablesDifferentTypes() {
        Object[] variables = new Object[] {"123", 123, 1245L, 12.25};
        assertEquals(Command.START.getText()
                        .concat(BotStringConstants.CALLBACK_DATA_SPLITTER)
                        .concat(Arrays.stream(variables).map(Object::toString)
                                        .collect(Collectors.joining(BotStringConstants.CALLBACK_DATA_SPLITTER))),
                CallbackQueryUtil.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithNullValue() {
        Object[] variables = new Object[] {"123", null, 1245L, 12.25};
        assertThrows(BaseException.class, () -> CallbackQueryUtil.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithEmptyStringValue() {
        Object[] variables = new Object[] {"123", 123, 1245L, ""};
        assertThrows(BaseException.class, () -> CallbackQueryUtil.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithBlankStringValue() {
        Object[] variables = new Object[] {"123", 123, 1245L, "       "};
        assertThrows(BaseException.class, () -> CallbackQueryUtil.buildCallbackData(Command.START, variables));
    }

    @Test
    void buildCallbackDataWithEmptyVariables() {
        assertThrows(BaseException.class, () -> CallbackQueryUtil.buildCallbackData(Command.START));
    }

    @Test
    void isBackReturnTrue() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(Command.BACK.name());
        update.setCallbackQuery(callbackQuery);
        assertTrue(CallbackQueryUtil.isBack(update));
    }

    @Test
    void isBackReturnFalse() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(Command.START.name());
        update.setCallbackQuery(callbackQuery);
        assertFalse(CallbackQueryUtil.isBack(update));
        callbackQuery.setData(null);
        assertFalse(CallbackQueryUtil.isBack(update));
        callbackQuery.setData("qwe");
        assertFalse(CallbackQueryUtil.isBack(update));
        callbackQuery.setData("");
        assertFalse(CallbackQueryUtil.isBack(update));
    }

    @Test
    void getSplitLongData() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        long l = 1000L;
        callbackQuery.setData(CallbackQueryUtil.buildCallbackData(Command.START, l));
        update.setCallbackQuery(callbackQuery);
        assertEquals(l, CallbackQueryUtil.getSplitLongData(update, 1));
        l = 0;
        callbackQuery.setData(CallbackQueryUtil.buildCallbackData(Command.BOT_OFFED, "123", l));
        assertEquals(l, CallbackQueryUtil.getSplitLongData(update, 2));
    }

    @Test
    void getSplitBooleanDataReturnTrue() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(CallbackQueryUtil.buildCallbackData(Command.START, "true"));
        update.setCallbackQuery(callbackQuery);
        assertTrue(CallbackQueryUtil.getSplitBooleanData(update, 1));
        callbackQuery.setData(CallbackQueryUtil.buildCallbackData(Command.START, 123, 123L, "qwe", "true"));
        assertTrue(CallbackQueryUtil.getSplitBooleanData(update, 4));
    }

    @Test
    void getSplitBooleanDataReturnFalse() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(CallbackQueryUtil.buildCallbackData(Command.START, "false"));
        update.setCallbackQuery(callbackQuery);
        assertFalse(CallbackQueryUtil.getSplitBooleanData(update, 1));
    }

    @Test
    void getSplitDataByUpdate() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(CallbackQueryUtil.buildCallbackData(Command.START, 123, "qwerty", false));
        update.setCallbackQuery(callbackQuery);
        assertAll(
                () -> assertEquals("123", CallbackQueryUtil.getSplitData(update, 1)),
                () -> assertEquals("qwerty", CallbackQueryUtil.getSplitData(update, 2)),
                () -> assertEquals("false", CallbackQueryUtil.getSplitData(update, 3))
        );
    }

    @Test
    void getSplitDataByCallbackQuery() {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(CallbackQueryUtil.buildCallbackData(Command.START, 123, "qwerty", false));
        assertAll(
                () -> assertEquals("123", CallbackQueryUtil.getSplitData(callbackQuery, 1)),
                () -> assertEquals("qwerty", CallbackQueryUtil.getSplitData(callbackQuery, 2)),
                () -> assertEquals("false", CallbackQueryUtil.getSplitData(callbackQuery, 3))
        );
    }
}