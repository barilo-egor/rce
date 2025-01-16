package tgb.btc.rce.service.handler.impl.callback.request.withdrawal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawalOfFundsHandlerTest {

    @Mock
    private IWithdrawalRequestService withdrawalRequestService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private VariablePropertiesReader variablePropertiesReader;

    @Mock
    private IReadUserService readUserService;

    @Mock
    private IMessagePropertiesService messagePropertiesService;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @InjectMocks
    private WithdrawalOfFundsHandler withdrawalOfFundsHandler;

    @ParameterizedTest
    @ValueSource(longs = {1, 10, 540})
    @DisplayName("Должен сообщить, что уже есть активная заявка.")
    @SuppressWarnings({"unchecked"})
    void shouldAnswerAlreadyHasRequest(Long activeRequestsSize) {
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

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        String newData = "newData";
        Long requestPid = 553320L;

        when(withdrawalRequestService.getActiveByUserChatId(chatId)).thenReturn(activeRequestsSize);
        when(keyboardBuildService.buildInline(anyList())).thenReturn(inlineKeyboardMarkup);
        when(withdrawalRequestService.getPidByUserChatId(chatId)).thenReturn(requestPid);
        when(callbackDataService.buildData(CallbackQueryData.DELETE_WITHDRAWAL_REQUEST, requestPid)).thenReturn(newData);
        withdrawalOfFundsHandler.handle(callbackQuery);

        ArgumentCaptor<List<InlineButton>> buttonListCaptor = ArgumentCaptor.forClass(List.class);
        verify(keyboardBuildService).buildInline(buttonListCaptor.capture());
        List<InlineButton> buttons = buttonListCaptor.getValue();
        assertAll(
                () -> assertEquals(1, buttons.size()),
                () -> assertEquals("Удалить", buttons.get(0).getText()),
                () -> assertEquals(newData, buttons.get(0).getData())
        );
        verify(responseSender).sendMessage(chatId, "У вас уже есть активная заявка.", inlineKeyboardMarkup);
        verify(responseSender, times(0)).deleteMessage(anyLong(), anyInt());
    }

    @ParameterizedTest
    @CsvSource({
            "459, 500",
            "1, 2",
            "1000, 205444"
    })
    @DisplayName("Должен сообщить о минимальной сумме вывода.")
    void shouldAnswerMinSum(Integer referralBalance, Integer minSum) {
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

        when(withdrawalRequestService.getActiveByUserChatId(chatId)).thenReturn(0L);
        when(variablePropertiesReader.getInt(VariableType.REFERRAL_MIN_SUM)).thenReturn(minSum);
        when(readUserService.getReferralBalanceByChatId(chatId)).thenReturn(referralBalance);

        withdrawalOfFundsHandler.handle(callbackQuery);

        verify(responseSender).sendMessage(chatId, "Минимальная сумма для вывода средств равна " + minSum + "₽");
        verify(responseSender, times(0)).deleteMessage(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Должен запросить контакт.")
    @SuppressWarnings({"unchecked"})
    void handle() {
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

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        String messageText = "message";

        when(withdrawalRequestService.getActiveByUserChatId(chatId)).thenReturn(0L);
        when(variablePropertiesReader.getInt(VariableType.REFERRAL_MIN_SUM)).thenReturn(10);
        when(readUserService.getReferralBalanceByChatId(chatId)).thenReturn(100);
        when(keyboardBuildService.buildReply(anyList())).thenReturn(replyKeyboardMarkup);
        when(messagePropertiesService.getMessage(PropertiesMessage.WITHDRAWAL_ASK_CONTACT)).thenReturn(messageText);

        withdrawalOfFundsHandler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(redisUserStateService).save(chatId, UserState.WITHDRAWAL_OF_FUNDS);
        ArgumentCaptor<List<ReplyButton>> buttonListCaptor = ArgumentCaptor.forClass(List.class);
        verify(keyboardBuildService).buildReply(buttonListCaptor.capture());
        List<ReplyButton> buttons = buttonListCaptor.getValue();
        assertAll(
                () -> assertEquals(2, buttons.size()),
                () -> assertEquals("Поделиться контактом", buttons.get(0).getText()),
                () -> assertTrue(buttons.get(0).isRequestContact()),
                () -> assertFalse(buttons.get(0).isRequestLocation()),
                () -> assertEquals(BotReplyButton.CANCEL.getButton(), buttons.get(1))
        );
        verify(responseSender).sendMessage(chatId, messageText, replyKeyboardMarkup);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.WITHDRAWAL_OF_FUNDS, withdrawalOfFundsHandler.getCallbackQueryData());
    }
}