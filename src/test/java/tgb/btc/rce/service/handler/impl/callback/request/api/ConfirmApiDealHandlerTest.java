package tgb.btc.rce.service.handler.impl.callback.request.api;

import org.junit.jupiter.api.DisplayName;
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
import tgb.btc.api.web.INotifier;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmApiDealHandlerTest {

    @Mock
    private IApiDealService apiDealService;

    @Mock
    private IGroupChatService groupChatService;

    @Mock
    private INotifier notifier;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private ConfirmApiDealHandler confirmApiDealHandler;

    @Test
    @DisplayName("Должен отменить подтверждение и сообщить, что не установлена группа для вывода запроса, в случае если вывод требуется.")
    void handleWithRequestWithoutGroupChat() {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        Long dealPid = 20550L;
        Long apiUserPid = 43055L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(true);
        when(apiDealService.getApiUserPidByDealPid(dealPid)).thenReturn(apiUserPid);
        when(groupChatService.hasGroupChat(apiUserPid)).thenReturn(false);
        confirmApiDealHandler.handle(callbackQuery);
        verify(responseSender).sendAnswerCallbackQuery(Integer.toString(callbackQueryId), """
                            Не найдена установленная группа для вывода запросов этого клиента. \
                            Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в \
                            разделе "API пользователи".
                            """, true);
    }


    @ParameterizedTest
    @EnumSource(value = ApiDealStatus.class, names = "PAID", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Для всех статусов не PAID должен сообщить, что заявка уже обработана.")
    void handleAlreadyHandledDeal(ApiDealStatus status) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        Long dealPid = 20550L;
        Long apiUserPid = 43055L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(true);
        when(apiDealService.getApiUserPidByDealPid(dealPid)).thenReturn(apiUserPid);
        when(groupChatService.hasGroupChat(apiUserPid)).thenReturn(true);
        when(apiDealService.getApiDealStatusByPid(dealPid)).thenReturn(status);
        confirmApiDealHandler.handle(callbackQuery);
        verify(responseSender).sendMessage(chatId, "Заявка уже обработана.");
    }

    @ParameterizedTest
    @EnumSource(ApiDealType.class)
    @DisplayName("Должен подтвердить заявку без вывода в группу")
    void handleWithoutRequest(ApiDealType type) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        Long dealPid = 20550L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(false);
        when(apiDealService.getApiDealStatusByPid(dealPid)).thenReturn(ApiDealStatus.PAID);
        when(apiDealService.getApiDealTypeByPid(dealPid)).thenReturn(type);
        confirmApiDealHandler.handle(callbackQuery);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(apiDealService).updateApiDealStatusByPid(ApiDealStatus.ACCEPTED, dealPid);
        verify(notifier, times(0)).sendRequestToWithdrawApiDeal(dealPid);
        String expectedMessage = ApiDealType.API.equals(type)
                ? "API сделка подтверждена."
                : "Диспут подтвержден.";
        verify(responseSender).sendMessage(chatId, expectedMessage);
    }

    @ParameterizedTest
    @EnumSource(ApiDealType.class)
    @DisplayName("Должен подтвердить заявку с выводом в группу")
    void handleWithRequest(ApiDealType type) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        Long dealPid = 20550L;
        Long apiUserPid = 43055L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(true);
        when(apiDealService.getApiDealStatusByPid(dealPid)).thenReturn(ApiDealStatus.PAID);
        when(apiDealService.getApiDealTypeByPid(dealPid)).thenReturn(type);
        when(apiDealService.getApiUserPidByDealPid(dealPid)).thenReturn(apiUserPid);
        when(groupChatService.hasGroupChat(apiUserPid)).thenReturn(true);
        confirmApiDealHandler.handle(callbackQuery);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(apiDealService).updateApiDealStatusByPid(ApiDealStatus.ACCEPTED, dealPid);
        verify(notifier).sendRequestToWithdrawApiDeal(dealPid);
        String expectedMessage = ApiDealType.API.equals(type)
                ? "API сделка подтверждена."
                : "Диспут подтвержден.";
        verify(responseSender).sendMessage(chatId, expectedMessage);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CONFIRM_API_DEAL, confirmApiDealHandler.getCallbackQueryData());
    }
}