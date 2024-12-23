package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.create;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.impl.util.CallbackDataService;

import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveRequisiteHandlerTest {

    @Mock
    private CallbackDataService callbackDataService;

    @Mock
    private IPaymentTypeService paymentTypeService;

    @Mock
    private IPaymentRequisiteService paymentRequisiteService;

    @Mock
    private ResponseSender responseSender;

    @InjectMocks
    private SaveRequisiteHandler saveRequisiteHandler;

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("Реквизит: \uD83D\uDED12200152951294373 СБП АЛЬФА БАНК\nФиатная валюта: Рос.рубли\nТип оплаты: АЛЬФА", "🛑2200152951294373 СБП АЛЬФА БАНК"),
                Arguments.of("Реквизит: \uD83D\uDED11111 2222 3333 4444 СБП АЛЬФА БАНК\nФиатная валюта: Рос.рубли\nТип оплаты: АЛЬФА", "🛑1111 2222 3333 4444 СБП АЛЬФА БАНК"),
                Arguments.of("Реквизит: 1\nФиатная валюта: Рос.рубли\nТип оплаты: АЛЬФА", "1")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void handle(String messageTest, String expectedRequisite) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        message.setText(messageTest);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        Long paymentTypePid = 244L;
        PaymentType paymentType = new PaymentType();
        paymentType.setPid(paymentTypePid);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentTypePid);
        when(paymentTypeService.getByPid(paymentTypePid)).thenReturn(paymentType);

        saveRequisiteHandler.handle(callbackQuery);

        ArgumentCaptor<PaymentRequisite> captor = ArgumentCaptor.forClass(PaymentRequisite.class);
        verify(paymentRequisiteService).save(captor.capture());
        PaymentRequisite paymentRequisite = captor.getValue();
        assertAll(
                () -> assertEquals(expectedRequisite, paymentRequisite.getRequisite()),
                () -> assertTrue(paymentRequisite.getOn()),
                () -> assertEquals(paymentTypePid, paymentRequisite.getPaymentType().getPid())
        );
        verify(paymentRequisiteService).removeOrder(paymentTypePid);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Реквизит успешно сохранен:\n <b>" + paymentRequisite.getRequisite() + "</b>");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SAVE_REQUISITE, saveRequisiteHandler.getCallbackQueryData());
    }
}