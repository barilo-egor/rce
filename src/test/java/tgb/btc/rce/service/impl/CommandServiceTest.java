package tgb.btc.rce.service.impl;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.impl.util.CommandService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandServiceTest {

    private final IUpdateService updateService = new UpdateService();

    private final ButtonsDesignPropertiesReader buttonsDesignPropertiesReader = new ButtonsDesignPropertiesReader();

    private final CommandService commandService = new CommandService(updateService, buttonsDesignPropertiesReader);


    @Test
    void isStartCommandReturnTrue() {
        Update update = new Update();
        Message message = new Message();
        message.setText(commandService.getText(Command.START));
        update.setMessage(message);
        assertTrue(commandService.isStartCommand(update));
    }

    @Test
    void isStartCommandReturnFalse() {
        Update update = new Update();
        Message message = new Message();
        message.setText(commandService.getText(Command.BACKUP_DB));
        update.setMessage(message);
        assertFalse(commandService.isStartCommand(update));
    }

    @Test
    void isStartCommandReturnFalseForNoMessage() {
        Update update = new Update();
        assertFalse(commandService.isStartCommand(update));
    }

    @Test
    void isStartCommandReturnFalseForMessageWithoutText() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);
        assertFalse(commandService.isStartCommand(update));
    }

    @Test
    void isSubmitLoginCommand() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setData(Command.SUBMIT_LOGIN.name());
        assertTrue(commandService.isSubmitCommand(update));
    }

    @Test
    void isSubmitRegisterCommand() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        update.setCallbackQuery(callbackQuery);
        callbackQuery.setData(Command.SUBMIT_REGISTER.name());
        assertTrue(commandService.isSubmitCommand(update));
    }

    @Test
    void isSubmitCommandReturnFalseWithoutCallbackQuery() {
        Update update = new Update();
        assertFalse(commandService.isSubmitCommand(update));
    }

    @Test
    void isSubmitCommandReturnFalse() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(Command.START.name());
        update.setCallbackQuery(callbackQuery);
        assertFalse(commandService.isSubmitCommand(update));
    }
}