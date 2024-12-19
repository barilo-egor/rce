package tgb.btc.rce.service.handler.impl.callback.request.withdrawal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowWithdrawalRequestHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private WithdrawalOfFundsService withdrawalOfFundsService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @Mock
    private IWithdrawalRequestService withdrawalRequestService;

    @InjectMocks
    private ShowWithdrawalRequestHandler handler;

    @ParameterizedTest
    @ValueSource(longs = {1, 15110, 214550323})
    @SuppressWarnings({"unchecked"})
    void handle(Long requestPid) {
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

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setPid(requestPid);
        String messageText = "message";
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        String newData = "newData";

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(requestPid);
        when(withdrawalRequestService.findById(requestPid)).thenReturn(withdrawalRequest);
        when(withdrawalOfFundsService.toString(withdrawalRequest)).thenReturn(messageText);
        when(keyboardBuildService.buildInline(anyList())).thenReturn(inlineKeyboardMarkup);
        when(callbackDataService.buildData(CallbackQueryData.HIDE_WITHDRAWAL, requestPid)).thenReturn(newData);

        handler.handle(callbackQuery);

        ArgumentCaptor<List<InlineButton>> buttonListCaptor = ArgumentCaptor.forClass(List.class);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(keyboardBuildService).buildInline(buttonListCaptor.capture());
        List<InlineButton> buttonList = buttonListCaptor.getValue();
        assertAll(
                () -> assertEquals(1, buttonList.size()),
                () -> assertEquals("Скрыть", buttonList.get(0).getText()),
                () -> assertEquals(newData, buttonList.get(0).getData()),
                () -> assertEquals(InlineType.CALLBACK_DATA, buttonList.get(0).getInlineType())
        );
        verify(responseSender).sendMessage(chatId, messageText, inlineKeyboardMarkup);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SHOW_WITHDRAWAL_REQUEST, handler.getCallbackQueryData());
    }
}