package tgb.btc.rce.util;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandUtilTest {

    @Test
    void isStartCommandReturnTrue() {
        Update update = new Update();
        Message message = new Message();
        message.setText(Command.START.getText());
        update.setMessage(message);
        assertTrue(CommandUtil.isStartCommand(update));
    }

    @Test
    void isStartCommandReturnFalse() {
        Update update = new Update();
        Message message = new Message();
        message.setText(Command.BACKUP_DB.getText());
        update.setMessage(message);
        assertFalse(CommandUtil.isStartCommand(update));
    }

    @Test
    void isStartCommandReturnFalseForNoMessage() {
        Update update = new Update();
        assertFalse(CommandUtil.isStartCommand(update));
    }

    @Test
    void isStartCommandReturnFalseForMessageWithoutText() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);
        assertFalse(CommandUtil.isStartCommand(update));
    }

    @Test
    void isSubmitLoginCommand() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setData(Command.SUBMIT_LOGIN.name());
        assertTrue(CommandUtil.isSubmitCommand(update));
    }

    @Test
    void isSubmitRegisterCommand() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setData(Command.SUBMIT_REGISTER.name());
        assertTrue(CommandUtil.isSubmitCommand(update));
    }

    @Test
    void isSubmitCommandReturnFalseWithoutCallbackQuery() {
        Update update = new Update();
        assertFalse(CommandUtil.isSubmitCommand(update));
    }

    @Test
    void isSubmitCommandReturnFalse() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(Command.START.name());
        update.setCallbackQuery(callbackQuery);
        assertFalse(CommandUtil.isSubmitCommand(update));
    }
}