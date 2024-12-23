package tgb.btc.rce.service.handler.impl.callback.settings.payment.create;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.bot.DealType;
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
class DealTypeCreatePaymentTypeHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IRedisStringService redisStringService;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private IKeyboardService keyboardService;

    @InjectMocks
    private DealTypeCreatePaymentTypeHandler dealTypeCreatePaymentTypeHandler;

    @ParameterizedTest
    @CsvSource({
            "BUY, BYN",
            "SELL, BYN",
            "BUY, RUB",
            "SELL, RUB"
    })
    void handle(String dealTypeString, String fiatCurrencyString) {
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

        FiatCurrency fiatCurrency = FiatCurrency.valueOf(fiatCurrencyString);
        DealType dealType = DealType.valueOf(dealTypeString);
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();

        when(callbackDataService.getArgument(data, 1)).thenReturn(dealType.name());
        when(callbackDataService.getArgument(data, 2)).thenReturn(fiatCurrency.name());
        when(keyboardService.getReplyCancel()).thenReturn(replyKeyboard);

        dealTypeCreatePaymentTypeHandler.handle(callbackQuery);

        verify(redisStringService).save(RedisPrefix.DEAL_TYPE, chatId, dealType.name());
        verify(redisStringService).save(RedisPrefix.FIAT_CURRENCY, chatId, fiatCurrency.name());
        verify(redisUserStateService).save(chatId, UserState.CREATE_PAYMENT_TYPE);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Введите название для нового типа оплаты.", replyKeyboard);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DEAL_TYPE_CREATE_PAYMENT_TYPE, dealTypeCreatePaymentTypeHandler.getCallbackQueryData());
    }
}