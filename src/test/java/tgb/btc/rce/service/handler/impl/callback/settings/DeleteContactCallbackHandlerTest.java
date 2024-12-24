package tgb.btc.rce.service.handler.impl.callback.settings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteContactCallbackHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IContactService contactService;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private DeleteContactCallbackHandler handler;

    @ParameterizedTest
    @ValueSource(strings = {"Гугл", "О", "\uD83D\uDE20 Чат-канал", "\uD83D\uDFEA"})
    void handle(String label) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        Long contactPid = 222444L;
        Contact contact = new Contact();
        contact.setPid(contactPid);
        contact.setLabel(label);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(contactPid);
        when(contactService.findById(contactPid)).thenReturn(contact);

        handler.handle(callbackQuery);

        verify(contactService).deleteById(contactPid);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Контакт <b>" + label + "</b> успешно удален.");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DELETE_CONTACT, handler.getCallbackQueryData());
    }
}