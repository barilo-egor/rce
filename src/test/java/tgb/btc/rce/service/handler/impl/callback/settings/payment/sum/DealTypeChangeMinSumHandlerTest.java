package tgb.btc.rce.service.handler.impl.callback.settings.payment.sum;

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
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DealTypeChangeMinSumHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IPaymentTypeService paymentTypeService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private DealTypeChangeMinSumHandler dealTypeChangeMinSumHandler;

    @ParameterizedTest
    @CsvSource({
        "BUY, BYN",
        "SELL, BYN",
        "BUY, RUB"
    })
    @DisplayName("Должен сообщить о пустом списке оплат.")
    void handleWithoutPaymentTypes(DealType dealType, FiatCurrency fiatCurrency) {
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

        dealTypeChangeMinSumHandler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Список тип оплат на " + dealType.getAccusative() + "(" + fiatCurrency.getDisplayName() + ") пуст.");
        verify(responseSender, times(0)).sendEditedMessageText(anyLong(), anyInt(), anyString(), anyList());
    }

    @ParameterizedTest
    @CsvSource({
            "BUY, BYN",
            "SELL, BYN",
            "BUY, RUB"
    })
    @DisplayName("Должен вывести типы оплат.")
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
            when(callbackDataService.buildData(CallbackQueryData.CHANGE_MIN_SUM, (long) i)).thenReturn(data + i);
        }
        inlineButtons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());

        when(callbackDataService.getArgument(data, 1)).thenReturn(dealType.name());
        when(callbackDataService.getArgument(data, 2)).thenReturn(fiatCurrency.name());
        when(paymentTypeService.getByDealTypeAndFiatCurrency(dealType, fiatCurrency)).thenReturn(paymentTypes);

        dealTypeChangeMinSumHandler.handle(callbackQuery);

        verify(responseSender).sendEditedMessageText(chatId, messageId, "Выберите тип оплаты(<b>" + dealType.getNominativeFirstLetterToUpper()
                + "</b>, <b>" + fiatCurrency.getDisplayName() + "</b>) для изменения минимальной суммы.", inlineButtons);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DEAL_TYPE_CHANGE_MIN_SUM, dealTypeChangeMinSumHandler.getCallbackQueryData());
    }
}