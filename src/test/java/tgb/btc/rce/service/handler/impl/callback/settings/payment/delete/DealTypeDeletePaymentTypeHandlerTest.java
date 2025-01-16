package tgb.btc.rce.service.handler.impl.callback.settings.payment.delete;

import static org.junit.jupiter.api.Assertions.*;

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
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealTypeDeletePaymentTypeHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IPaymentTypeService paymentTypeService;

    @Mock
    private IAdminPanelService adminPanelService;

    @InjectMocks
    private DealTypeDeletePaymentTypeHandler dealTypeDeletePaymentTypeHandler;

    @ParameterizedTest
    @CsvSource({
            "BUY, BYN",
            "SELL, RUB",
            "BUY, RUB",
            "SELL, BYN"
    })
    @DisplayName("Должен сообщить об отсутствии типов оплат.")
    void handleNoPaymentTypes(DealType dealType, FiatCurrency fiatCurrency) {
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

        when(callbackDataService.getArgument(data, 1)).thenReturn(dealType.name());
        when(callbackDataService.getArgument(data, 2)).thenReturn(fiatCurrency.name());
        when(paymentTypeService.getByDealTypeAndFiatCurrency(dealType, fiatCurrency)).thenReturn(new ArrayList<>());

        dealTypeDeletePaymentTypeHandler.handle(callbackQuery);

        verify(paymentTypeService).getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId,
                "Список тип оплат на " + dealType.getAccusative() + "(" + fiatCurrency.getDisplayName() + ") пуст.");
        verify(adminPanelService).send(chatId);
    }

    @ParameterizedTest
    @CsvSource({
            "BUY, BYN",
            "SELL, RUB",
            "BUY, RUB",
            "SELL, BYN"
    })
    @DisplayName("Должен вывести список типов оплат для удаления.")
    void handleWithPaymentTypes(DealType dealType, FiatCurrency fiatCurrency) {
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

        List<PaymentType> paymentTypes = new ArrayList<>();
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String name = "name " + i;
            PaymentType paymentType = PaymentType.builder()
                    .name(name)
                    .fiatCurrency(fiatCurrency)
                    .dealType(dealType)
                    .minSum(BigDecimal.ZERO)
                    .build();
            paymentType.setPid((long) i);
            paymentTypes.add(paymentType);
            inlineButtons.add(InlineButton.builder()
                            .text(name)
                            .data(data + i)
                    .build());
            when(callbackDataService.buildData(CallbackQueryData.DELETING_PAYMENT_TYPE, (long) i)).thenReturn(data + i);
        }
        inlineButtons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());

        when(callbackDataService.getArgument(data, 1)).thenReturn(dealType.name());
        when(callbackDataService.getArgument(data, 2)).thenReturn(fiatCurrency.name());
        when(paymentTypeService.getByDealTypeAndFiatCurrency(dealType, fiatCurrency)).thenReturn(paymentTypes);

        dealTypeDeletePaymentTypeHandler.handle(callbackQuery);

        verify(paymentTypeService).getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
        verify(responseSender).sendEditedMessageText(chatId, messageId,
                "Выберите тип оплаты(<b>" + dealType.getNominativeFirstLetterToUpper()
                + "</b>, <b>" + fiatCurrency.getDisplayName() + "</b>) для удаления.", inlineButtons);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DEAL_TYPE_DELETE_PAYMENT_TYPE, dealTypeDeletePaymentTypeHandler.getCallbackQueryData());
    }
}