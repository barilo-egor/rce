package tgb.btc.rce.service.handler.impl.callback.settings.payment.sum;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FiatChangeMinSumHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IKeyboardService keyboardService;

    @InjectMocks
    private FiatChangeMinSumHandler handler;

    @ParameterizedTest
    @EnumSource(FiatCurrency.class)
    void handle(FiatCurrency fiatCurrency) {
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

        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();

        when(callbackDataService.getArgument(data, 1)).thenReturn(fiatCurrency.name());
        when(keyboardService.getInlineDealTypes(CallbackQueryData.DEAL_TYPE_CHANGE_MIN_SUM, fiatCurrency)).thenReturn(replyKeyboard);

        handler.handle(callbackQuery);

        verify(keyboardService).getInlineDealTypes(CallbackQueryData.DEAL_TYPE_CHANGE_MIN_SUM, fiatCurrency);
        verify(responseSender).sendEditedMessageText(chatId, messageId, "Выберите тип сделки для изменения минимальной суммы.",
                replyKeyboard);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.FIAT_CHANGE_MIN_SUM, handler.getCallbackQueryData());
    }
}