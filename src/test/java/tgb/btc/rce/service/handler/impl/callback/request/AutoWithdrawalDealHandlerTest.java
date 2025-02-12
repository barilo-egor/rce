package tgb.btc.rce.service.handler.impl.callback.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoWithdrawalDealHandlerTest {

    @Mock
    private IReadDealService readDealService;

    @Mock
    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private AutoWithdrawalDealHandler autoWithdrawalDealHandler;

    @ParameterizedTest
    @CsvSource({
            "0.001, 0.01",
            "2.435, 2.436",
            "20000, 5000000"
    })
    @DisplayName("Должен сообщить, что на балансе недостаточно средств.")
    void shouldAnswerInsufficientFunds(String balance, Double amount) {
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

        Long dealPid = 1000L;
        CryptoCurrency cryptoCurrency = CryptoCurrency.BITCOIN;

        Deal deal = new Deal();
        deal.setPid(dealPid);
        deal.setCryptoAmount(new BigDecimal(amount));
        deal.setCryptoCurrency(cryptoCurrency);

        Message deleteMessage = new Message();
        Integer deleteMessageId = 24440;
        deleteMessage.setMessageId(deleteMessageId);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(responseSender.sendMessage(chatId, "Получение баланса.")).thenReturn(Optional.of(deleteMessage));
        when(cryptoWithdrawalService.getBalance(cryptoCurrency)).thenReturn(new BigDecimal(balance));

        autoWithdrawalDealHandler.handle(callbackQuery);

        verify(responseSender).sendMessage(chatId, "Получение баланса.");
        verify(cryptoWithdrawalService).getBalance(cryptoCurrency);
        verify(responseSender).deleteMessage(chatId, deleteMessageId);
        verify(responseSender).sendAnswerCallbackQuery(Integer.toString(callbackQueryId),
                "На балансе недостаточно средств для автовывода. Текущий баланс: " + balance, true);
        verify(responseSender, times(0)).sendMessage(anyLong(), anyString(), any(InlineButton.class));
    }

    @ParameterizedTest
    @CsvSource({
            "0.01, 0.001",
            "2.436, 2.435",
            "200000000, 5000"
    })
    @DisplayName("Должен запросить подтверждение автовывода.")
    void shouldAskConfirm(String balance, String amount) {
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

        Long dealPid = 1000L;
        CryptoCurrency cryptoCurrency = CryptoCurrency.BITCOIN;
        String wallet = "wallet";

        Deal deal = new Deal();
        deal.setPid(dealPid);
        deal.setCryptoAmount(new BigDecimal(amount));
        deal.setCryptoCurrency(cryptoCurrency);
        deal.setWallet(wallet);

        Message deleteMessage = new Message();
        Integer deleteMessageId = 24440;
        deleteMessage.setMessageId(deleteMessageId);

        String newData = "newData";

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(false);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(responseSender.sendMessage(chatId, "Получение баланса.")).thenReturn(Optional.of(deleteMessage));
        when(cryptoWithdrawalService.getBalance(cryptoCurrency)).thenReturn(new BigDecimal(balance));
        when(callbackDataService.buildData(CallbackQueryData.CONFIRM_AUTO_WITHDRAWAL_DEAL, deal.getPid(), messageId, false)).thenReturn(newData);

        autoWithdrawalDealHandler.handle(callbackQuery);

        verify(responseSender).sendMessage(chatId, "Получение баланса.");
        verify(cryptoWithdrawalService).getBalance(cryptoCurrency);
        verify(responseSender).deleteMessage(chatId, deleteMessageId);
        verify(responseSender, times(0)).sendAnswerCallbackQuery(anyString(), anyString(), anyBoolean());
        verify(responseSender).sendMessage(eq(chatId), eq("Вы собираетесь отправить " + amount
                + " " + cryptoCurrency.getShortName() + " на адрес <code>" + wallet + "</code>. Продолжить?"),
                eq(List.of(InlineButton.builder()
                        .text("Продолжить")
                        .data(newData).build(), InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build())));
    }

    @ParameterizedTest
    @CsvSource({
            "BITCOIN, 0.001, btc213mk12424n124j142k12n4",
            "LITECOIN, 2.51, ltc2qwe1sgd6j8yuj1jfg6fdg8",
            "MONERO, 0.2, xmr5hfd4hf6h7ht6trh8h4er6qwe4",
    })
    @DisplayName("Должен правильно сформировать сообщение запроса подтверждения на вывод.")
    void shouldBuildMessage(String cryptoCurrencyName, String amount, String wallet) {
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

        Long dealPid = 1000L;
        CryptoCurrency cryptoCurrency = CryptoCurrency.valueOf(cryptoCurrencyName);

        Deal deal = new Deal();
        deal.setPid(dealPid);
        deal.setCryptoAmount(new BigDecimal(amount));
        deal.setCryptoCurrency(cryptoCurrency);
        deal.setWallet(wallet);

        Message deleteMessage = new Message();
        Integer deleteMessageId = 24440;
        deleteMessage.setMessageId(deleteMessageId);

        String newData = "newData";

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(callbackDataService.getBoolArgument(data, 2)).thenReturn(false);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(responseSender.sendMessage(chatId, "Получение баланса.")).thenReturn(Optional.of(deleteMessage));
        when(cryptoWithdrawalService.getBalance(cryptoCurrency)).thenReturn(new BigDecimal("500"));
        when(callbackDataService.buildData(CallbackQueryData.CONFIRM_AUTO_WITHDRAWAL_DEAL, dealPid, messageId, false)).thenReturn(newData);

        autoWithdrawalDealHandler.handle(callbackQuery);

        verify(responseSender).sendMessage(eq(chatId), eq("Вы собираетесь отправить " + amount
                        + " " + cryptoCurrency.getShortName() + " на адрес <code>" + wallet + "</code>. Продолжить?"),
                eq(List.of(InlineButton.builder()
                        .text("Продолжить")
                        .data(newData).build(), InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build())));
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.AUTO_WITHDRAWAL_DEAL, autoWithdrawalDealHandler.getCallbackQueryData());
    }
}