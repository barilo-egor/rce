package tgb.btc.rce.service.handler.impl.callback.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdditionalVerificationHandlerTest {

    @Mock
    private IDealUserService dealUserService;

    @Mock
    private IModifyDealService modifyDealService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IModifyUserService modifyUserService;

    @Mock
    private IKeyboardBuildService keyboardBuildService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @InjectMocks
    private AdditionalVerificationHandler additionalVerificationHandler;

    @ParameterizedTest
    @CsvSource(value = {
            "1, 12314124",
            "10523, 540545364",
            "5050332, 553321343"
    })
    @SuppressWarnings({"unchecked"})
    void handle(Long dealPid, Long userChatId) {
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

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(dealUserService.getUserChatIdByDealPid(dealPid)).thenReturn(userChatId);
        when(keyboardBuildService.buildReply(anyList())).thenReturn(replyKeyboardMarkup);

        additionalVerificationHandler.handle(callbackQuery);

        verify(modifyDealService).updateDealStatusByPid(DealStatus.AWAITING_VERIFICATION, dealPid);
        verify(redisUserStateService).save(userChatId, UserState.ADDITIONAL_VERIFICATION);
        verify(modifyUserService).updateBufferVariable(userChatId, dealPid.toString());
        ArgumentCaptor<List<ReplyButton>> buttonListCaptor = ArgumentCaptor.forClass(List.class);
        verify(keyboardBuildService).buildReply(buttonListCaptor.capture());
        List<ReplyButton> value = buttonListCaptor.getValue();
        assertAll(
                () -> assertEquals(1, value.size()),
                () -> assertEquals("Отказаться от верификации", value.get(0).getText()),
                () -> assertFalse(value.get(0).isRequestContact()),
                () -> assertFalse(value.get(0).isRequestLocation())
        );
        verify(responseSender).sendMessage(userChatId,
                "⚠️Уважаемый клиент, необходимо пройти дополнительную верификацию. Предоставьте фото карты " +
                "с которой была оплата на фоне переписки с ботом, либо бумажного чека на фоне переписки с " +
                "ботом для завершения сделки. (Проверка проходится только при первом обмене)",
                replyKeyboardMarkup);
        verify(responseSender).sendMessage(chatId, "Дополнительная верификация запрошена.");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.ADDITIONAL_VERIFICATION, additionalVerificationHandler.getCallbackQueryData());
    }
}