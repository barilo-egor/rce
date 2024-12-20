package tgb.btc.rce.service.handler.impl.callback.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAndBlockUserHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IModifyDealService modifyDealService;

    @Mock
    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IStartService startService;

    @Mock
    private IDealUserService dealUserService;

    @Mock
    private IDealPropertyService dealPropertyService;

    private final String botUsername = "testbot";

    private DeleteAndBlockUserHandler deleteAndBlockUserHandler;

    @BeforeEach
    void setUp() {
        this.deleteAndBlockUserHandler = new DeleteAndBlockUserHandler(callbackDataService, modifyDealService,
                cryptoWithdrawalService, responseSender, startService, dealUserService, dealPropertyService, botUsername);
    }

    @Test
    @DisplayName("Должен сообщить что заявка уже подтверждена.")
    void handlerAlreadyConfirmed() {
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

        Long dealPid = 23555L;
        DealStatus dealStatus = DealStatus.CONFIRMED;

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(dealPropertyService.getDealStatusByPid(dealPid)).thenReturn(dealStatus);

        deleteAndBlockUserHandler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Заявка уже подтверждена, удаление невозможно.");
        verify(modifyDealService, times(0)).deleteDeal(dealPid, true);
    }

    @ParameterizedTest
    @EnumSource(value = DealStatus.class, names = "CONFIRMED", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Должен удалить сделку для статусов не CONFIRMED.")
    void handle(DealStatus dealStatus) {
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

        Long dealPid = 23555L;
        Long userChatId = 987654321L;

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(dealPropertyService.getDealStatusByPid(dealPid)).thenReturn(dealStatus);
        when(dealUserService.getUserChatIdByDealPid(dealPid)).thenReturn(userChatId);

        deleteAndBlockUserHandler.handle(callbackQuery);

        verify(responseSender, times(0)).sendMessage(chatId, "Заявка уже подтверждена, удаление невозможно.");
        verify(modifyDealService).deleteDeal(dealPid, true);
        verify(responseSender).sendMessage(chatId, "Заявка №23555 удалена.");
        verify(startService).process(userChatId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DELETE_DEAL_AND_BLOCK_USER, deleteAndBlockUserHandler.getCallbackQueryData());
    }
}