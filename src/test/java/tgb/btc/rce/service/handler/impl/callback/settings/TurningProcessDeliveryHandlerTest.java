package tgb.btc.rce.service.handler.impl.callback.settings;

import static org.junit.jupiter.api.Assertions.*;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurningProcessDeliveryHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IModule<DeliveryKind> deliveryKindModule;

    @Mock
    private IKeyboardService keyboardService;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private TurningProcessDeliveryHandler handler;

    @ParameterizedTest
    @EnumSource(DeliveryKind.class)
    void handle(DeliveryKind deliveryKind) {
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

        InlineButton inlineButton = InlineButton.builder().text("text").build();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        when(callbackDataService.getArgument(data, 1)).thenReturn(deliveryKind.name());
        when(keyboardService.getDeliveryTypeButton()).thenReturn(inlineButton);
        when(keyboardBuildService.buildInline(List.of(inlineButton))).thenReturn(inlineKeyboardMarkup);

        handler.handle(callbackQuery);

        verify(deliveryKindModule).set(deliveryKind);
        verify(responseSender).sendEditedMessageText(chatId, messageId, "Вкл/выкл способов доставки", inlineKeyboardMarkup);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.TURN_PROCESS_DELIVERY, handler.getCallbackQueryData());
    }
}