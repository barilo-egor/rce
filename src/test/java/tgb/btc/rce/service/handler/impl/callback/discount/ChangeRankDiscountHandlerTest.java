package tgb.btc.rce.service.handler.impl.callback.discount;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import tgb.btc.library.bean.bot.UserDiscount;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeRankDiscountHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IReadUserService readUserService;

    @Mock
    private IUserDiscountService userDiscountService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @InjectMocks
    private ChangeRankDiscountHandler changeRankDiscountHandler;

    @Test
    void handleShouldCreateNewRankDiscount() {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        Integer messageId = 45500;
        message.setMessageId(messageId);
        callbackQuery.setMessage(message);
        Long chatId = 123456789L;
        Long userPid = 50000L;
        User user = new User();
        user.setId(chatId);
        callbackQuery.setFrom(user);
        String data = "data";
        callbackQuery.setData(data);
        Long userChatId = 987654321L;
        boolean isRankDiscountOn = true;
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(isRankDiscountOn);
        when(readUserService.getPidByChatId(userChatId)).thenReturn(userPid);
        when(userDiscountService.isExistByUserPid(userPid)).thenReturn(false);
        when(callbackDataService.buildData(CallbackQueryData.CHANGE_RANK_DISCOUNT, userChatId, !isRankDiscountOn)).thenReturn(data);
        when(keyboardBuildService.buildInline(anyList())).thenReturn(replyKeyboard);
        changeRankDiscountHandler.handle(callbackQuery);
        ArgumentCaptor<UserDiscount> userDiscountArgumentCaptor = ArgumentCaptor.forClass(UserDiscount.class);
        verify(userDiscountService, times(1)).save(userDiscountArgumentCaptor.capture());
        UserDiscount userDiscount = userDiscountArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(userPid, userDiscount.getUser().getPid()),
                () -> assertEquals(isRankDiscountOn, userDiscount.getIsRankDiscountOn())
        );
        verify(responseSender, times(1)).deleteMessage(chatId, messageId);
        verify(callbackDataService, times(1)).buildData(CallbackQueryData.CHANGE_RANK_DISCOUNT,userChatId, !isRankDiscountOn);
        verify(keyboardBuildService, times(1)).buildInline(
                List.of(InlineButton.builder().text("Выключить").data(data).build())
        );
        verify(responseSender, times(1)).sendMessage(chatId, "Пользователь chat id=" + userChatId + ".", replyKeyboard);
    }

    @Test
    void handleShouldUpdateRankDiscount() {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        Integer messageId = 45500;
        message.setMessageId(messageId);
        callbackQuery.setMessage(message);
        Long chatId = 123456789L;
        Long userPid = 50000L;
        User user = new User();
        user.setId(chatId);
        callbackQuery.setFrom(user);
        String data = "data";
        callbackQuery.setData(data);
        Long userChatId = 987654321L;
        boolean isRankDiscountOn = false;
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(isRankDiscountOn);
        when(readUserService.getPidByChatId(userChatId)).thenReturn(userPid);
        when(userDiscountService.isExistByUserPid(userPid)).thenReturn(true);
        when(callbackDataService.buildData(CallbackQueryData.CHANGE_RANK_DISCOUNT, userChatId, !isRankDiscountOn)).thenReturn(data);
        when(keyboardBuildService.buildInline(anyList())).thenReturn(replyKeyboard);
        changeRankDiscountHandler.handle(callbackQuery);
        verify(userDiscountService, times(1)).updateIsRankDiscountOnByPid(isRankDiscountOn, userPid);
        verify(responseSender, times(1)).deleteMessage(chatId, messageId);
        verify(keyboardBuildService, times(1)).buildInline(List.of(InlineButton.builder()
                .text("Включить").data(data).build()));
        verify(responseSender, times(1)).sendMessage(chatId, "Пользователь chat id=" + userChatId + ".", replyKeyboard);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CHANGE_RANK_DISCOUNT, changeRankDiscountHandler.getCallbackQueryData());
    }
}