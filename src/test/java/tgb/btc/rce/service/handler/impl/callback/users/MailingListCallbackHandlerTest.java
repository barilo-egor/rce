package tgb.btc.rce.service.handler.impl.callback.users;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.rce.enums.BotSystemMessage;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.processors.support.MessagesService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailingListCallbackHandlerTest {

    @Mock
    private MessagesService messagesService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private MailingListCallbackHandler handler;

    @ParameterizedTest
    @ValueSource(strings = {
            """
                                        🚀Сегодня в нашем чате будет проходить новая мини игра‼️
                    
                            💵Денежные призы + купоны на баланс бота🤖
                    
                            👊Присоединяйся\s
                            https://t.me/+fgUsdfqeqwe1231YzQy
                    """,
            """
                                        🎉🎄 НОВОГОДНЯЯ РУЛЕТКА с @BULeqqBTC 🎰🎁
                    
                    ✨ Старт уже 19.12.24 — не пропусти! 🗓
                    
                    💰 Денежные призы и купончики на баланс бота ждут тебя! 💳🎉
                    
                    Для участия:
                    📩 Перейди в канал https://t.me/BULBA_213NFO , где ты найдешь пост с инструкцией. Выполнив её, отписывай админу @priqwessa_legenda 🧑‍🎄 и запишись на игру!
                    
                    🔔 Не упусти шанс выиграть! 🎉👇🏻
                    Присоединяйся к чату — жми на ссылку!
                    👉🏻 https://t.me/+fgUI5gds32e8M1YzQy
                    
                    ✨ Пусть Новый год будет полон сюрпризов и выигрышей! 🎊
                    """,
            """
                    1
                    """})
    void handle(String messageText) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        message.setText(messageText);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        handler.handle(callbackQuery);
        verify(messagesService).sendMessageToUsers(chatId, messageText);
        verify(responseSender).sendMessage(chatId, "Рассылка запущена. По окончанию вам придет оповещение.");
        verify(responseSender).sendEditedMessageText(chatId, messageId, messageText + BotSystemMessage.MESSAGE_SENT.getText());
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.MAILING_LIST, handler.getCallbackQueryData());
    }
}