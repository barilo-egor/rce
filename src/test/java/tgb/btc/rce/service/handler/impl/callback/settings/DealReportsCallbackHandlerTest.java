package tgb.btc.rce.service.handler.impl.callback.settings;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDateDealService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.util.ILoadReportService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DealReportsCallbackHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IDateDealService dateDealService;

    @Mock
    private IApiDealService apiDealService;

    @Mock
    private ILoadReportService loadReportService;

    @Mock
    private IKeyboardService keyboardService;

    @InjectMocks
    private DealReportsCallbackHandler handler;

    @Test
    @DisplayName("Должен сгенерировать отчет за сегодня.")
    void handleToday() {
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

        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowDateTime = LocalDateTime.now();

        List<Deal> deals = new ArrayList<>();
        List<ApiDeal> apiDeals = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Deal deal = new Deal();
            deal.setPid((long) i);
            deals.add(deal);
            ApiDeal apiDeal = new ApiDeal();
            apiDeal.setPid((long) i);
            apiDeals.add(apiDeal);
        }

        when(callbackDataService.getArgument(data, 1)).thenReturn(DealReportsCallbackHandler.TODAY);
        when(dateDealService.getConfirmedByDateBetween(nowDate)).thenReturn(deals);
        when(apiDealService.getAcceptedByDate(nowDateTime)).thenReturn(apiDeals);
        try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class);
             MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(nowDate);
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(nowDateTime);

            handler.handle(callbackQuery);

            verify(responseSender).deleteMessage(chatId, messageId);
            verify(dateDealService).getConfirmedByDateBetween(nowDate);
            verify(apiDealService).getAcceptedByDate(nowDateTime);
        }
        verify(loadReportService).loadReport(deals, chatId, DealReportsCallbackHandler.TODAY, apiDeals);
    }

    @Test
    @DisplayName("Должен сгенерировать отчет за десять дней.")
    void handleTenDays() {
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

        LocalDate nowDate = Mockito.mock(LocalDate.class);
        LocalDateTime nowDateTime = Mockito.mock(LocalDateTime.class);

        List<Deal> deals = new ArrayList<>();
        List<ApiDeal> apiDeals = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Deal deal = new Deal();
            deal.setPid((long) i);
            deals.add(deal);
            ApiDeal apiDeal = new ApiDeal();
            apiDeal.setPid((long) i);
            apiDeals.add(apiDeal);
        }

        when(callbackDataService.getArgument(data, 1)).thenReturn(DealReportsCallbackHandler.TEN_DAYS);
        when(dateDealService.getConfirmedByDateBetween(nowDate.minusDays(10), nowDate)).thenReturn(deals);
        when(apiDealService.getAcceptedByDateBetween(nowDateTime.minusDays(10), nowDateTime)).thenReturn(apiDeals);
        try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class);
             MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(nowDate);
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(nowDateTime);

            LocalDate beforeTenDays = nowDate.minusDays(10);
            when(nowDate.minusDays(10)).thenReturn(beforeTenDays);

            LocalDateTime beforeTenDaysDateTime = nowDateTime.minusDays(10);
            when(nowDateTime.minusDays(10)).thenReturn(beforeTenDaysDateTime);

            handler.handle(callbackQuery);

            verify(responseSender).deleteMessage(chatId, messageId);
            verify(dateDealService).getConfirmedByDateBetween(beforeTenDays, nowDate);
            verify(apiDealService).getAcceptedByDateBetween(beforeTenDaysDateTime, nowDateTime);
        }
        verify(loadReportService).loadReport(deals, chatId, DealReportsCallbackHandler.TEN_DAYS, apiDeals);
    }

    @Test
    @DisplayName("Должен сгенерировать отчет за месяц.")
    void handleMonth() {
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

        LocalDate nowDate = Mockito.mock(LocalDate.class);
        LocalDateTime nowDateTime = Mockito.mock(LocalDateTime.class);

        List<Deal> deals = new ArrayList<>();
        List<ApiDeal> apiDeals = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Deal deal = new Deal();
            deal.setPid((long) i);
            deals.add(deal);
            ApiDeal apiDeal = new ApiDeal();
            apiDeal.setPid((long) i);
            apiDeals.add(apiDeal);
        }

        when(callbackDataService.getArgument(data, 1)).thenReturn(DealReportsCallbackHandler.MONTH);
        when(dateDealService.getConfirmedByDateBetween(nowDate.minusDays(30), nowDate)).thenReturn(deals);
        when(apiDealService.getAcceptedByDateBetween(nowDateTime.minusDays(30), nowDateTime)).thenReturn(apiDeals);
        try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class);
             MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(nowDate);
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(nowDateTime);

            LocalDate beforeMonth = nowDate.minusDays(30);
            when(nowDate.minusDays(30)).thenReturn(beforeMonth);

            LocalDateTime beforeMonthDateTime = nowDateTime.minusDays(30);
            when(nowDateTime.minusDays(30)).thenReturn(beforeMonthDateTime);

            handler.handle(callbackQuery);

            verify(responseSender).deleteMessage(chatId, messageId);
            verify(dateDealService).getConfirmedByDateBetween(beforeMonth, nowDate);
            verify(apiDealService).getAcceptedByDateBetween(beforeMonthDateTime, nowDateTime);
        }
        verify(loadReportService).loadReport(deals, chatId, DealReportsCallbackHandler.MONTH, apiDeals);
    }

    @Test
    @DisplayName("Должен запросить дату.")
    void handleDate() {
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

        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();

        when(callbackDataService.getArgument(data, 1)).thenReturn(DealReportsCallbackHandler.DATE);
        when(keyboardService.getReplyCancel()).thenReturn(replyKeyboard);

        handler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Введите дату в формате <b>31.01.2000</b> для выгрузки отчета по сделкам.",
                replyKeyboard);
        verify(redisUserStateService).save(chatId, UserState.DATE_DEAL_REPORT);
        verify(loadReportService, times(0)).loadReport(anyList(), anyLong(), anyString(), anyList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Послезавтра", " ", "", "30"})
    @DisplayName("Должен пробросить исключение")
    void handleThrows(String period) {
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

        when(callbackDataService.getArgument(data, 1)).thenReturn(period);

        assertThrows(BaseException.class, () -> handler.handle(callbackQuery));
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DEAL_REPORTS, handler.getCallbackQueryData());
    }
    
}