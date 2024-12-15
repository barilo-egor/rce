package tgb.btc.rce.service.handler.impl.callback.request.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelApiDealHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IApiDealService apiDealService;

    @InjectMocks
    private CancelApiDealHandler cancelApiDealHandler;

    @Test
    void handleApiDeal() {
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
        when(apiDealService.getApiDealTypeByPid(dealPid)).thenReturn(ApiDealType.API);
        cancelApiDealHandler.handle(callbackQuery);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(apiDealService).updateApiDealStatusByPid(ApiDealStatus.DECLINED, dealPid);
        verify(responseSender).sendMessage(chatId, "API сделка отменена.");
    }

    @Test
    void handleApiDispute() {
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
        when(apiDealService.getApiDealTypeByPid(dealPid)).thenReturn(ApiDealType.DISPUTE);
        cancelApiDealHandler.handle(callbackQuery);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(apiDealService).updateApiDealStatusByPid(ApiDealStatus.DECLINED, dealPid);
        verify(responseSender).sendMessage(chatId, "Диспут отменен.");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CANCEL_API_DEAL, cancelApiDealHandler.getCallbackQueryData());
    }
}