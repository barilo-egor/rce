package tgb.btc.rce.service.handler.impl.callback.settings.delete;

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
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.impl.callback.settings.payment.delete.DealTypeDeletePaymentTypeHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletingPaymentTypeHandlerTest {

    @Mock
    private IPaymentTypeService paymentTypeService;

    @Mock
    private IPaymentRequisiteService paymentRequisiteService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private DealTypeDeletePaymentTypeHandler dealTypeDeletePaymentTypeHandler;

    @Mock
    private IModifyDealService modifyDealService;

    @InjectMocks
    private DeletingPaymentTypeHandler deletingPaymentTypeHandler;

    @ParameterizedTest
    @CsvSource({
            "Some payment type, BUY, BYN"
    })
    void handle(String paymentTypeName, DealType dealType, FiatCurrency fiatCurrency) {
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

        Long paymentTypePid = 4433L;
        PaymentType paymentType = new PaymentType();
        paymentType.setPid(paymentTypePid);
        paymentType.setName(paymentTypeName);
        paymentType.setDealType(dealType);
        paymentType.setFiatCurrency(fiatCurrency);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentTypePid);
        when(paymentTypeService.getByPid(paymentTypePid)).thenReturn(paymentType);

        deletingPaymentTypeHandler.handle(callbackQuery);

        verify(modifyDealService).updatePaymentTypeToNullByPaymentTypePid(paymentTypePid);
        verify(paymentRequisiteService).deleteByPaymentTypePid(paymentTypePid);
        verify(paymentRequisiteService).removeOrder(paymentTypePid);
        verify(paymentTypeService).deleteById(paymentTypePid);
        verify(responseSender).sendMessage(chatId, "Тип оплаты на " + dealType.getAccusative() + " \"" + paymentType.getName() + "\" удален.");
        verify(dealTypeDeletePaymentTypeHandler).sendPaymentTypes(chatId, messageId, dealType, fiatCurrency);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DELETING_PAYMENT_TYPE, deletingPaymentTypeHandler.getCallbackQueryData());
    }
}