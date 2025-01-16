package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.create;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentTypeCreateRequisiteHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IRedisStringService redisStringService;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IKeyboardService keyboardService;

    @InjectMocks
    private PaymentTypeCreateRequisiteHandler paymentTypeCreateRequisiteHandler;

    @ParameterizedTest
    @CsvSource({
            "BYN, 25555",
            "RUB, 1"
    })
    void handle(FiatCurrency fiatCurrency, Long paymentTypePid) {
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

        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();

        when(callbackDataService.getArgument(data, 1)).thenReturn(fiatCurrency.name());
        when(callbackDataService.getLongArgument(data, 2)).thenReturn(paymentTypePid);
        when(keyboardService.getReplyCancel()).thenReturn(replyKeyboard);

        paymentTypeCreateRequisiteHandler.handle(callbackQuery);

        verify(redisStringService).save(RedisPrefix.FIAT_CURRENCY, chatId, fiatCurrency.name());
        verify(redisStringService).save(RedisPrefix.PAYMENT_TYPE_PID, chatId, paymentTypePid.toString());
        verify(redisUserStateService).save(chatId, UserState.CREATE_REQUISITE);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Введите создаваемый реквизит.", replyKeyboard);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.PAYMENT_TYPE_CREATE_REQUISITE, paymentTypeCreateRequisiteHandler.getCallbackQueryData());
    }
}