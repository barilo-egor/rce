package tgb.btc.rce.service.handler.impl.callback.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.impl.web.Notifier;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmAutoWithdrawalDealHandlerTest {

    @Mock
    private IGroupChatService groupChatService;

    @Mock
    private IModifyDealService modifyDealService;

    @Mock
    private Notifier notifier;

    @Mock
    private IReadDealService readDealService;

    @Mock
    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IReadUserService readUserService;

    private final String botUsername = "usernameBot";

    private ConfirmAutoWithdrawalDealHandler confirmAutoWithdrawalDealHandler;

    @BeforeEach
    void setUp() {
        confirmAutoWithdrawalDealHandler = new ConfirmAutoWithdrawalDealHandler(groupChatService, modifyDealService,
                notifier, readDealService, cryptoWithdrawalService, responseSender, callbackDataService, readUserService, botUsername);
    }

    @Test
    @DisplayName("Должен сообщить, что не установлена группа для автовывода сделок.")
    void shouldAnswerNoAutoWithdrawalGroup() {
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

        when(groupChatService.hasAutoWithdrawal()).thenReturn(false);
        confirmAutoWithdrawalDealHandler.handle(callbackQuery);
        verify(responseSender).sendAnswerCallbackQuery(Integer.toString(callbackQueryId),
                """
                            Не найдена установленная группа для автовывода сделок. \
                            Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в \
                            разделе "Сделки из бота".
                            """, true);
        verify(callbackDataService, times(0)).getLongArgument(anyString(), anyInt());
    }

    @ParameterizedTest
    @CsvSource({
            "1, some error",
            "53503, number format exception error message",
            "12000553,  "
    })
    @DisplayName("Должен сообщить об ошибке, в случае исключения.")
    void shouldAnswerWithErrorAfterException(Long dealPid, String exceptionMessage) {
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

        Message deleteMessage = new Message();
        Integer deleteMessageId = 554345;
        deleteMessage.setMessageId(deleteMessageId);

        when(groupChatService.hasAutoWithdrawal()).thenReturn(true);
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(responseSender.sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.")).thenReturn(Optional.of(deleteMessage));
        when(readDealService.findByPid(dealPid)).thenThrow(new BaseException(exceptionMessage));
        confirmAutoWithdrawalDealHandler.handle(callbackQuery);
        verify(responseSender, times(0)).sendAnswerCallbackQuery(anyString(), anyString(), anyBoolean());
        verify(responseSender).sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.");
        verify(responseSender).sendMessage(chatId, "Ошибка при попытке автовывода сделки " + dealPid + ": " + exceptionMessage);
        verify(responseSender).deleteMessage(chatId, deleteMessageId);
    }

    @Test
    @DisplayName("Должен сообщить, что сделка уже подтверждена.")
    void shouldAnswerDealAlreadyConfirmed() {
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

        Message deleteMessage = new Message();
        Integer deleteMessageId = 554345;
        deleteMessage.setMessageId(deleteMessageId);

        Long dealPid = 25251L;
        Deal deal = new Deal();
        deal.setPid(dealPid);
        deal.setDealStatus(DealStatus.CONFIRMED);

        when(groupChatService.hasAutoWithdrawal()).thenReturn(true);
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(responseSender.sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.")).thenReturn(Optional.of(deleteMessage));
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        confirmAutoWithdrawalDealHandler.handle(callbackQuery);
        verify(responseSender, times(0)).sendAnswerCallbackQuery(anyString(), anyString(), anyBoolean());
        verify(responseSender).sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.");
        verify(responseSender).sendMessage(chatId, "Сделка уже находится в статусе \"Подтверждена\".");
        verify(responseSender).deleteMessage(chatId, deleteMessageId);
    }

    @ParameterizedTest
    @CsvSource({
            "BITCOIN, 0.001, btc213j1in5325j235kl2n35, qwer",
            "LITECOIN, 1.22, ltc124513n5i5n32k5n325jk3n5, SuperUserName443",
            "USDT, 54, usdt152155weer5m353mn,  "
    })
    void shouldWithdrawal(String cryptoCurrencyString, String cryptoAmountString, String wallet, String username) {
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

        Message deleteMessage = new Message();
        Integer deleteMessageId = 554345;
        deleteMessage.setMessageId(deleteMessageId);

        Integer lastMessageId = 525412;
        String hash = "asd87as5f876asf5687sd5g78s6d5g8sd6g87sd6g86gwe8t6we87rk9jhl86jk8;7576jl5etwert346346";
        Long dealPid = 25251L;
        Deal deal = new Deal();
        deal.setPid(dealPid);
        CryptoCurrency cryptoCurrency = CryptoCurrency.valueOf(cryptoCurrencyString);
        deal.setCryptoCurrency(cryptoCurrency);
        BigDecimal cryptoAmount = new BigDecimal(cryptoAmountString);
        deal.setCryptoAmount(cryptoAmount);
        deal.setWallet(wallet);
        deal.setDealStatus(DealStatus.PAID);

        when(groupChatService.hasAutoWithdrawal()).thenReturn(true);
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(responseSender.sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.")).thenReturn(Optional.of(deleteMessage));
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(cryptoWithdrawalService.withdrawal(cryptoCurrency, cryptoAmount, wallet)).thenReturn(hash);
        when(readUserService.getUsernameByChatId(chatId)).thenReturn(username);
        when(callbackDataService.getIntArgument(data, 2)).thenReturn(lastMessageId);
        confirmAutoWithdrawalDealHandler.handle(callbackQuery);
        verify(responseSender, times(0)).sendAnswerCallbackQuery(anyString(), anyString(), anyBoolean());
        verify(responseSender).sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.");
        verify(cryptoWithdrawalService).withdrawal(cryptoCurrency, cryptoAmount, wallet);
        verify(modifyDealService).confirm(dealPid, hash);
        verify(cryptoWithdrawalService).deleteFromPool(botUsername, dealPid);
        if (Objects.isNull(username) || username.isBlank()) {
            verify(notifier).sendAutoWithdrawDeal("бота", "chatid:" + chatId, dealPid);
        } else {
            verify(notifier).sendAutoWithdrawDeal("бота", username, dealPid);
        }
        verify(responseSender).deleteMessage(chatId, lastMessageId);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Транзакция сделки №" + dealPid + "\n" + String.format(CryptoCurrency.BITCOIN.getHashUrl(), hash));
        verify(responseSender).deleteMessage(chatId, deleteMessageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CONFIRM_AUTO_WITHDRAWAL_DEAL, confirmAutoWithdrawalDealHandler.getCallbackQueryData());
    }
}