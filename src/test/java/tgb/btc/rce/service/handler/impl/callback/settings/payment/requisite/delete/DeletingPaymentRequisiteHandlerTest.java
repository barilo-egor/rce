package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.delete;

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
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IShowRequisitesService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletingPaymentRequisiteHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IPaymentRequisiteService paymentRequisiteService;

    @Mock
    private IShowRequisitesService showRequisitesService;

    @InjectMocks
    private DeletingPaymentRequisiteHandler deletingPaymentRequisiteHandler;

    @ParameterizedTest
    @CsvSource({
            "255, some requisite",
            "1, s",
            "50002444, \uD83D\uDED12200152951294373 СБП АЛЬФА БАНК"
    })
    void handle(Long paymentRequisitePid, String requisite) {
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

        Long paymentTypePid = 5122L;

        PaymentRequisite paymentRequisite = new PaymentRequisite();
        paymentRequisite.setPid(paymentRequisitePid);
        paymentRequisite.setRequisite(requisite);

        PaymentType paymentType = new PaymentType();
        paymentType.setPid(paymentTypePid);
        paymentRequisite.setPaymentType(paymentType);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentRequisitePid);
        when(paymentRequisiteService.findById(paymentRequisitePid)).thenReturn(paymentRequisite);

        deletingPaymentRequisiteHandler.handle(callbackQuery);

        verify(paymentRequisiteService).delete(paymentRequisite);
        verify(paymentRequisiteService).removeOrder(paymentTypePid);
        verify(responseSender).sendMessage(chatId, "Реквизит <b>" + requisite + "</b> удален.");
        verify(showRequisitesService).showForDelete(chatId, paymentTypePid, messageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DELETING_PAYMENT_TYPE_REQUISITE, deletingPaymentRequisiteHandler.getCallbackQueryData());
    }
}