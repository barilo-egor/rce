package tgb.btc.rce.service.handler.impl.callback.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmUserDealHandlerTest {

    @Mock
    private IModifyDealService modifyDealService;

    @Mock
    private INotifier notifier;

    @Mock
    private IGroupChatService groupChatService;

    @Mock
    private IDealUserService dealUserService;

    @Mock
    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IStartService startService;

    @Mock
    private IReadUserService readUserService;

    @Mock
    private IRedisUserStateService redisUserStateService;

    private final String botUsername = "usernameBot";

    private ConfirmUserDealHandler confirmUserDealHandler;

    @BeforeEach
    void setUp() {
        confirmUserDealHandler = new ConfirmUserDealHandler(modifyDealService, notifier, groupChatService,
                dealUserService, cryptoWithdrawalService, callbackDataService, responseSender, startService,
                readUserService, redisUserStateService, botUsername);
    }

    @Test
    @DisplayName("Должен сообщить об отсутствии группы для вывода сделок.")
    void shouldAnswerNotDealRequestGroup() {
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

        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(true);
        when(groupChatService.hasDealRequests()).thenReturn(false);
        confirmUserDealHandler.handle(callbackQuery);
        verify(responseSender).sendAnswerCallbackQuery(Integer.toString(callbackQueryId),
                """
                            Не найдена установленная группа для вывода запросов. \
                            Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в \
                            разделе "Сделки из бота".
                            """, true);
        verify(callbackDataService, times(0)).getLongArgument(anyString(), anyInt());
    }

    @ParameterizedTest
    @EnumSource(UserState.class)
    void handleWithoutRequest(UserState userState) {
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

        Long dealPid = 50234L;
        Long userChatId = 987654321L;

        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(false);
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(dealUserService.getUserChatIdByDealPid(dealPid)).thenReturn(userChatId);
        when(redisUserStateService.get(userChatId)).thenReturn(userState);
        confirmUserDealHandler.handle(callbackQuery);
        verify(responseSender, times(0)).sendAnswerCallbackQuery(anyString(), anyString(), anyBoolean());
        verify(modifyDealService).confirm(dealPid);
        if (UserState.ADDITIONAL_VERIFICATION.equals(userState)) {
            verify(startService).process(userChatId);
        }
        verify(responseSender).deleteMessage(chatId, messageId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"b", "superusernameofuser123", " "})
    void handleWithRequest(String username) {
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

        Long dealPid = 50234L;
        Long userChatId = 987654321L;

        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(true);
        when(groupChatService.hasDealRequests()).thenReturn(true);
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(dealUserService.getUserChatIdByDealPid(dealPid)).thenReturn(userChatId);
        when(redisUserStateService.get(userChatId)).thenReturn(UserState.DEAL);
        when(readUserService.getUsernameByChatId(chatId)).thenReturn(username);
        confirmUserDealHandler.handle(callbackQuery);
        verify(responseSender, times(0)).sendAnswerCallbackQuery(anyString(), anyString(), anyBoolean());
        verify(modifyDealService).confirm(dealPid);
        if (Objects.isNull(username) || username.isBlank()) {
            verify(notifier).sendRequestToWithdrawDeal("бота", "chatid:" + chatId, dealPid);
        } else {
            verify(notifier).sendRequestToWithdrawDeal("бота", username, dealPid);
        }
        verify(responseSender).deleteMessage(chatId, messageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CONFIRM_USER_DEAL, confirmUserDealHandler.getCallbackQueryData());
    }
}