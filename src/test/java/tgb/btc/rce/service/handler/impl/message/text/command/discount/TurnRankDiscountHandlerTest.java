package tgb.btc.rce.service.handler.impl.message.text.command.discount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnRankDiscountHandlerTest {

    @Mock
    private VariablePropertiesReader variablePropertiesReader;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private TurnRankDiscountHandler handler;

    @Test
    @DisplayName("Должен предложить выключить ранговую скидку.")
    void handleWhenOn() {
        Message message = new Message();
        Long chatId = 123456789L;
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        String text = "text";
        message.setText(text);

        String data = "data";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        when(variablePropertiesReader.getBoolean(VariableType.DEAL_RANK_DISCOUNT_ENABLE)).thenReturn(true);
        when(callbackDataService.buildData(CallbackQueryData.TURNING_RANK_DISCOUNT, false)).thenReturn(data);
        when(keyboardBuildService.buildInline(anyList())).thenReturn(inlineKeyboardMarkup);

        handler.handle(message);

        verify(callbackDataService).buildData(CallbackQueryData.TURNING_RANK_DISCOUNT, false);
        verify(keyboardBuildService).buildInline(List.of(InlineButton.builder().text("Выключить").data(data).build()));
        verify(responseSender).sendMessage(chatId, "Ранговая скидка включена для всех. Выключить?", inlineKeyboardMarkup);
    }

    @Test
    @DisplayName("Должен предложить включить ранговую скидку.")
    void handleWhenOff() {
        Message message = new Message();
        Long chatId = 123456789L;
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        String text = "text";
        message.setText(text);

        String data = "data";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        when(variablePropertiesReader.getBoolean(VariableType.DEAL_RANK_DISCOUNT_ENABLE)).thenReturn(false);
        when(callbackDataService.buildData(CallbackQueryData.TURNING_RANK_DISCOUNT, true)).thenReturn(data);
        when(keyboardBuildService.buildInline(anyList())).thenReturn(inlineKeyboardMarkup);

        handler.handle(message);

        verify(callbackDataService).buildData(CallbackQueryData.TURNING_RANK_DISCOUNT, true);
        verify(keyboardBuildService).buildInline(List.of(InlineButton.builder().text("Включить").data(data).build()));
        verify(responseSender).sendMessage(chatId, "Ранговая скидка выключена для всех. Включить?", inlineKeyboardMarkup);
    }

    @Test
    void getTextCommand() {
        assertEquals(TextCommand.TURN_RANK_DISCOUNT, handler.getTextCommand());
    }
}