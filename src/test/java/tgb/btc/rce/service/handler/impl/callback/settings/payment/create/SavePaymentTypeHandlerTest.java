package tgb.btc.rce.service.handler.impl.callback.settings.payment.create;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavePaymentTypeHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IPaymentTypeService paymentTypeService;

    @InjectMocks
    private SavePaymentTypeHandler paymentTypeHandler;

    @ParameterizedTest
    @CsvSource(value = {
            "some payment type name, BUY, BYN",
            "s, SELL, RUB"
    })
    void handle(String name, DealType dealType, FiatCurrency fiatCurrency) {
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

        when(callbackDataService.getArgument(data, 1)).thenReturn(name);
        when(callbackDataService.getArgument(data, 2)).thenReturn(dealType.name());
        when(callbackDataService.getArgument(data, 3)).thenReturn(fiatCurrency.name());

        paymentTypeHandler.handle(callbackQuery);

        ArgumentCaptor<PaymentType> paymentTypeArgumentCaptor = ArgumentCaptor.forClass(PaymentType.class);
        verify(paymentTypeService).save(paymentTypeArgumentCaptor.capture());
        PaymentType paymentType = paymentTypeArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(name, paymentType.getName()),
                () -> assertEquals(dealType, paymentType.getDealType()),
                () -> assertEquals(fiatCurrency, paymentType.getFiatCurrency()),
                () -> assertEquals(BigDecimal.ZERO, paymentType.getMinSum())
        );
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Новый тип оплаты <b>" + name + "</b> сохранен. " +
                "Не забудьте установить минимальную сумму, добавить реквизиты и включить по необходимости.");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SAVE_PAYMENT_TYPE, paymentTypeHandler.getCallbackQueryData());
    }
}