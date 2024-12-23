package tgb.btc.rce.service.handler.impl.callback.settings.payment.turn;

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
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.util.IShowPaymentTypesService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurningPaymentTypeHandlerTest {

    @Mock
    private IPaymentTypeService paymentTypeService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IPaymentRequisiteService paymentRequisiteService;

    @Mock
    private IShowPaymentTypesService showPaymentTypesService;

    @InjectMocks
    private TurningPaymentTypeHandler turningPaymentTypeHandler;

    @ParameterizedTest
    @CsvSource(value = {
            "BUY, RUB, 1, true",
            "SELL, BYN, 5005, false",
            "SELL, RUB, 500003544, false"
    })
    void handleWhenOn(DealType dealType, FiatCurrency fiatCurrency, Long paymentTypePid, boolean isOn) {
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

        PaymentType paymentType = new PaymentType();
        paymentType.setPid(paymentTypePid);
        paymentType.setDealType(dealType);
        paymentType.setFiatCurrency(fiatCurrency);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentTypePid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(isOn);
        when(paymentTypeService.getByPid(paymentTypePid)).thenReturn(paymentType);

        turningPaymentTypeHandler.handle(callbackQuery);

        verify(paymentTypeService).updateIsOnByPid(isOn, paymentTypePid);
        verify(paymentRequisiteService).removeOrder(paymentTypePid);
        verify(showPaymentTypesService).sendForTurn(chatId, dealType, fiatCurrency, messageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.TURNING_PAYMENT_TYPES, turningPaymentTypeHandler.getCallbackQueryData());
    }
}