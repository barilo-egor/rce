package tgb.btc.rce.service.handler.impl.callback.request.pool;

import org.junit.jupiter.api.DisplayName;
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
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearPoolHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private ClearPoolHandler clearPoolHandler;

    @ParameterizedTest
    @ValueSource(longs = {1, 10, 5000})
    @DisplayName("Должен запросить подтверждение для разных количеств сделок.")
    @SuppressWarnings({"unchecked"})
    void handler(Long dealsSize) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        String newData = "newData";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealsSize);
        when(callbackDataService.buildData(CallbackQueryData.CONFIRM_CLEAR_POOL, messageId)).thenReturn(newData);
        when(keyboardBuildService.buildInline(anyList(), eq(2))).thenReturn(replyKeyboard);

        clearPoolHandler.handle(callbackQuery);

        verify(callbackDataService).buildData(CallbackQueryData.CONFIRM_CLEAR_POOL, messageId);
        ArgumentCaptor<List<InlineButton>> buttonListCaptor = ArgumentCaptor.forClass(List.class);
        verify(keyboardBuildService).buildInline(buttonListCaptor.capture(), eq(2));
        List<InlineButton> buttonList = buttonListCaptor.getValue();
        assertAll(
                () -> assertEquals("Да", buttonList.get(0).getText()),
                () -> assertEquals(newData, buttonList.get(0).getData()),
                () -> assertEquals("Нет", buttonList.get(1).getText()),
                () -> assertEquals(CallbackQueryData.INLINE_DELETE.name(), buttonList.get(1).getData())
        );
        verify(responseSender).sendMessage(chatId, "Вы собираетесь удалить все <b>"
                + dealsSize + "</b> сделок из BTC пула. Продолжить?", replyKeyboard);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CLEAR_POOL, clearPoolHandler.getCallbackQueryData());
    }
}