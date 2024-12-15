package tgb.btc.rce.service.handler.impl.callback.discount;

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
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurningRankDiscountHandlerTest {

    @Mock
    private VariablePropertiesReader variablePropertiesReader;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private TurningRankDiscountHandler turningRankDiscountHandler;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void handle(boolean newValue) {
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User();
        Long chatId = 123456789L;
        user.setId(chatId);
        callbackQuery.setFrom(user);
        Message message = new Message();
        Integer messageId = 55000;
        message.setMessageId(messageId);
        callbackQuery.setMessage(message);
        String data = "data";
        callbackQuery.setData(data);
        when(callbackDataService.getBoolArgument(data, 1)).thenReturn(newValue);
        turningRankDiscountHandler.handle(callbackQuery);
        verify(variablePropertiesReader).setProperty(VariableType.DEAL_RANK_DISCOUNT_ENABLE.getKey(), newValue);
        verify(responseSender).sendMessage(chatId, newValue ? "Скидка включена." : "Скидка выключена.");
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(variablePropertiesReader).reload();
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.TURNING_RANK_DISCOUNT, turningRankDiscountHandler.getCallbackQueryData());
    }
}