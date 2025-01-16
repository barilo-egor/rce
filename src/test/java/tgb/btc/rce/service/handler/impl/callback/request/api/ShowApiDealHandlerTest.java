package tgb.btc.rce.service.handler.impl.callback.request.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.process.IApiDealBotService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowApiDealHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IApiDealService apiDealService;

    @Mock
    private IApiDealBotService apiDealBotService;

    @InjectMocks
    private ShowApiDealHandler showApiDealHandler;

    @ParameterizedTest
    @EnumSource(value = ApiDealStatus.class, names = "PAID", mode = EnumSource.Mode.EXCLUDE)
    void handleNotPaid(ApiDealStatus status) {
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User();
        Message message = new Message();
        Long chatId = 123456789L;
        Integer messageId = 50500;
        user.setId(chatId);
        String data = "data";
        message.setMessageId(messageId);
        callbackQuery.setMessage(message);
        callbackQuery.setFrom(user);
        callbackQuery.setData(data);
        Long dealPid = 2005L;

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(apiDealService.getApiDealStatusByPid(dealPid)).thenReturn(status);
        showApiDealHandler.handle(callbackQuery);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Заявка уже обработана, либо отменена.");
        verify(apiDealBotService, times(0)).sendApiDeal(anyLong(), anyLong());
    }

    @Test
    void handlePaid() {
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User();
        Message message = new Message();
        Long chatId = 123456789L;
        Integer messageId = 50500;
        user.setId(chatId);
        String data = "data";
        message.setMessageId(messageId);
        callbackQuery.setMessage(message);
        callbackQuery.setFrom(user);
        callbackQuery.setData(data);
        Long dealPid = 2005L;
        ApiDealStatus status = ApiDealStatus.PAID;

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(apiDealService.getApiDealStatusByPid(dealPid)).thenReturn(status);
        showApiDealHandler.handle(callbackQuery);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender, times(0)).sendMessage(chatId, "Заявка уже обработана, либо отменена.");
        verify(apiDealBotService).sendApiDeal(anyLong(), anyLong());
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SHOW_API_DEAL, showApiDealHandler.getCallbackQueryData());
    }
}