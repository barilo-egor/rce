package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.dynamic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
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
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.ITurnDynamicRequisiteService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TurningDynamicRequisitesHandlerTest {

    @Mock
    private IPaymentTypeService paymentTypeService;

    @Mock
    private IPaymentRequisiteService paymentRequisiteService;

    @Mock
    private ITurnDynamicRequisiteService turnDynamicRequisiteService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private TurningDynamicRequisitesHandler turningDynamicRequisitesHandler;

    @ParameterizedTest
    @CsvSource({
            "1, BYN",
            "20555, BYN",
            "235550432123, RUB"
    })
    @DisplayName("Должен выключить динамику у типа оплаты.")
    void handleOff(Long paymentTypePid, FiatCurrency fiatCurrency) {
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
        paymentType.setFiatCurrency(fiatCurrency);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentTypePid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(false);
        when(paymentTypeService.getByPid(paymentTypePid)).thenReturn(paymentType);

        turningDynamicRequisitesHandler.handle(callbackQuery);

        verify(paymentTypeService).updateIsDynamicOnByPid(false, paymentTypePid);
        verify(turnDynamicRequisiteService).sendPaymentTypes(chatId, messageId, fiatCurrency);
        verify(paymentRequisiteService).removeOrder(paymentTypePid);
    }

    @ParameterizedTest
    @CsvSource({
            "1, BYN, 0",
            "20555, BYN, 1",
    })
    @DisplayName("Должен сообщить о недостаточном количестве реквизитов при включении.")
    void handleOnWithoutRequisites(Long paymentTypePid, FiatCurrency fiatCurrency, int requisitesCount) {
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
        paymentType.setFiatCurrency(fiatCurrency);

        List<PaymentRequisite> paymentRequisites = new ArrayList<>();
        for (int i = 0; i < requisitesCount; i++) {
            paymentRequisites.add(new PaymentRequisite());
        }

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentTypePid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(true);
        when(paymentTypeService.getByPid(paymentTypePid)).thenReturn(paymentType);
        when(paymentRequisiteService.getByPaymentType_Pid(paymentTypePid)).thenReturn(paymentRequisites);

        turningDynamicRequisitesHandler.handle(callbackQuery);

        verify(responseSender).sendMessage(chatId, "Недостаточно реквизитов для включения. Количество реквизитов: " + requisitesCount + ".");
        verify(paymentRequisiteService, times(0)).removeOrder(paymentTypePid);
    }

    @ParameterizedTest
    @CsvSource({
            "1, BYN, 2",
            "20555, BYN, 100",
    })
    @DisplayName("Должен сообщить о недостаточном количестве реквизитов при включении.")
    void handleOn(Long paymentTypePid, FiatCurrency fiatCurrency, int requisitesCount) {
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
        paymentType.setFiatCurrency(fiatCurrency);

        List<PaymentRequisite> paymentRequisites = new ArrayList<>();
        for (int i = 0; i < requisitesCount; i++) {
            paymentRequisites.add(new PaymentRequisite());
        }

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentTypePid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(true);
        when(paymentTypeService.getByPid(paymentTypePid)).thenReturn(paymentType);
        when(paymentRequisiteService.getByPaymentType_Pid(paymentTypePid)).thenReturn(paymentRequisites);

        turningDynamicRequisitesHandler.handle(callbackQuery);

        verify(paymentTypeService).updateIsDynamicOnByPid(true, paymentTypePid);
        verify(turnDynamicRequisiteService).sendPaymentTypes(chatId, messageId, fiatCurrency);
        verify(paymentRequisiteService).removeOrder(paymentTypePid);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.TURNING_DYNAMIC_REQUISITES, turningDynamicRequisitesHandler.getCallbackQueryData());
    }
}