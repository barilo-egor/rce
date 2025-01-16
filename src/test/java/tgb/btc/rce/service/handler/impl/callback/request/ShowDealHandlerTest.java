package tgb.btc.rce.service.handler.impl.callback.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowDealHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IReadDealService readDealService;

    @Mock
    private IReadUserService readUserService;

    @Mock
    private DealSupportService dealSupportService;

    @InjectMocks
    private ShowDealHandler showDealHandler;

    @Test
    @DisplayName("Должен сообщить, что заявка уже удалена.")
    void shouldAnswerDealDeleted() {
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

        Long dealPid = 535235L;

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);

        showDealHandler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Заявка была удалена.");
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"ADMIN", "OPERATOR"}, mode = EnumSource.Mode.INCLUDE)
    @DisplayName("Должен отправить заявку с кнопками управления и чеками для администратора и оператора.")
    void shouldSendRequestWithButtonsAndReceipts(UserRole userRole) {
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

        Long dealPid = 535235L;
        Deal deal = new Deal();
        deal.setPid(dealPid);
        String dealToString = """
                Заявка на покупку №244845\s
                Дата,время: 16-12-2024 21:44:15
                Тип оплаты: 💳ЛЮБОЙ БАНК РФ—СБП
                Кошелек: ltcqweqweq46wqwr1qw6r1qw
                Контакт: username
                Количество сделок: 1
                ID: 1111222233
                Курс: 121
                Сумма ltc: 0.2913
                Сумма: 4330 ₽
                Способ доставки: Обычная🙂25-80минут
                Реквизит:1324 6548 7945 6547
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        PaymentReceipt picture = new PaymentReceipt();
        picture.setPid(111L);
        picture.setReceiptFormat(ReceiptFormat.PICTURE);
        String pictureFileId = "qwe3e54qwe6q4t65w46er54hd6f5h4d98fj9fgj798ghk4g6hk41gh3k54s9d8g4d65";
        picture.setReceipt(pictureFileId);

        PaymentReceipt pdf = new PaymentReceipt();
        pdf.setPid(111L);
        pdf.setReceiptFormat(ReceiptFormat.PDF);
        String pdfFileId = "qwe3e54qwe6q4t65w46er54hd6f5h4d98fj9fgj798ghk4g6hk41gh3k54s9d8g4d65";
        pdf.setReceipt(pdfFileId);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(readUserService.getUserRoleByChatId(chatId)).thenReturn(userRole);
        when(dealSupportService.dealToString(dealPid)).thenReturn(dealToString);
        when(dealSupportService.dealToStringButtons(dealPid)).thenReturn(inlineKeyboardMarkup);
        when(readDealService.getPaymentReceipts(dealPid)).thenReturn(List.of(picture, pdf));

        showDealHandler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender, times(0)).sendMessage(chatId, "Заявка была удалена.");
        verify(responseSender).sendMessage(chatId, dealToString, inlineKeyboardMarkup);
        verify(responseSender).sendPhoto(chatId, "", pictureFileId);
        verify(responseSender).sendFile(chatId, new InputFile(pdfFileId));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"ADMIN", "OPERATOR"}, mode = EnumSource.Mode.INCLUDE)
    @DisplayName("Должен отправить заявку с кнопками управления и без чеков, в случае их отсутствия, для администратора и оператора.")
    void shouldSendRequestWithButtonsAndWithoutReceipts(UserRole userRole) {
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

        Long dealPid = 535235L;
        Deal deal = new Deal();
        deal.setPid(dealPid);
        String dealToString = """
                Заявка на покупку №244845\s
                Дата,время: 16-12-2024 21:44:15
                Тип оплаты: 💳ЛЮБОЙ БАНК РФ—СБП
                Кошелек: ltcqweqweq46wqwr1qw6r1qw
                Контакт: username
                Количество сделок: 1
                ID: 1111222233
                Курс: 121
                Сумма ltc: 0.2913
                Сумма: 4330 ₽
                Способ доставки: Обычная🙂25-80минут
                Реквизит:1324 6548 7945 6547
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(readUserService.getUserRoleByChatId(chatId)).thenReturn(userRole);
        when(dealSupportService.dealToString(dealPid)).thenReturn(dealToString);
        when(dealSupportService.dealToStringButtons(dealPid)).thenReturn(inlineKeyboardMarkup);
        when(readDealService.getPaymentReceipts(dealPid)).thenReturn(List.of());

        showDealHandler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender, times(0)).sendMessage(chatId, "Заявка была удалена.");
        verify(responseSender).sendMessage(chatId, dealToString, inlineKeyboardMarkup);
        verify(responseSender, times(0)).sendPhoto(anyLong(), anyString(), anyString());
        verify(responseSender, times(0)).sendFile(anyLong(), any(InputFile.class));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"ADMIN", "OPERATOR"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Должен отправить заявку без кнопок управления и чеков для  не администратора и не оператора.")
    void shouldSendRequestWithoutButtonsAndReceipts(UserRole userRole) {
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

        Long dealPid = 535235L;
        Deal deal = new Deal();
        deal.setPid(dealPid);
        String dealToString = """
                Заявка на покупку №244845\s
                Дата,время: 16-12-2024 21:44:15
                Тип оплаты: 💳ЛЮБОЙ БАНК РФ—СБП
                Кошелек: ltcqweqweq46wqwr1qw6r1qw
                Контакт: username
                Количество сделок: 1
                ID: 1111222233
                Курс: 121
                Сумма ltc: 0.2913
                Сумма: 4330 ₽
                Способ доставки: Обычная🙂25-80минут
                Реквизит:1324 6548 7945 6547
                """;
        PaymentReceipt paymentReceipt = new PaymentReceipt();
        paymentReceipt.setPid(111L);
        ReceiptFormat receiptFormat = ReceiptFormat.PICTURE;
        paymentReceipt.setReceiptFormat(receiptFormat);
        String receiptFileId = "qwe3e54qwe6q4t65w46er54hd6f5h4d98fj9fgj798ghk4g6hk41gh3k54s9d8g4d65";
        paymentReceipt.setReceipt(receiptFileId);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(readUserService.getUserRoleByChatId(chatId)).thenReturn(userRole);
        when(dealSupportService.dealToString(dealPid)).thenReturn(dealToString);

        showDealHandler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender, times(0)).sendMessage(chatId, "Заявка была удалена.");
        verify(responseSender).sendMessage(chatId, dealToString);
        verify(responseSender, times(0)).sendPhoto(anyLong(), anyString(), anyString());
        verify(responseSender, times(0)).sendFile(anyLong(), any(InputFile.class));
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SHOW_DEAL, showDealHandler.getCallbackQueryData());
    }
}