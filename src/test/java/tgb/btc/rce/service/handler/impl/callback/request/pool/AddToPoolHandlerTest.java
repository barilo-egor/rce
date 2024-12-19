package tgb.btc.rce.service.handler.impl.callback.request.pool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.library.service.bean.bot.deal.ReadDealService;
import tgb.btc.library.service.util.BigDecimalService;
import tgb.btc.library.vo.web.PoolDeal;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddToPoolHandlerTest {

    @Mock
    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Mock
    private IModifyDealService modifyDealService;

    @Mock
    private ReadDealService readDealService;

    @Mock
    private BigDecimalService bigDecimalService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    private AddToPoolHandler addToPoolHandler;

    private final String botUsername = "usernameBot";

    @BeforeEach
    void setUp() {
        addToPoolHandler = new AddToPoolHandler(cryptoWithdrawalService, modifyDealService, readDealService,
                bigDecimalService, responseSender, callbackDataService, botUsername);
    }

    @ParameterizedTest
    @EnumSource(CryptoCurrency.class)
    @DisplayName("Должен отправить сообщения исключения.")
    void shouldThrowApiResponseException(CryptoCurrency cryptoCurrency) {
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User();
        Message message = new Message();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        Long dealPid = 20550L;
        int callbackQueryId = 24005;

        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        String wallet = "wallet";
        BigDecimal cryptoAmount = new BigDecimal("0.001");
        String poolDealAmount = "0.001";

        Deal deal = new Deal();
        deal.setWallet(wallet);
        deal.setCryptoCurrency(cryptoCurrency);
        deal.setCryptoAmount(cryptoAmount);

        Message addingToPoolMessage = new Message();
        Integer addingToPoolMessageId = 50001;
        addingToPoolMessage.setMessageId(addingToPoolMessageId);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(responseSender.sendMessage(chatId, "Добавление сделки в пул, пожалуйста подождите.")).thenReturn(Optional.of(addingToPoolMessage));
        when(bigDecimalService.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())).thenReturn(poolDealAmount);
        String exceptionMessage = "Some error";
        when(cryptoWithdrawalService.addPoolDeal(any())).thenThrow(new ApiResponseErrorException(exceptionMessage));
        addToPoolHandler.handle(callbackQuery);
        verify(responseSender).sendMessage(chatId, exceptionMessage);
        verify(responseSender).deleteMessage(chatId, addingToPoolMessageId);
    }

    @ParameterizedTest
    @EnumSource(CryptoCurrency.class)
    void shouldHandlerDifferentCryptoCurrency(CryptoCurrency cryptoCurrency) {
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User();
        Message message = new Message();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        Long dealPid = 20550L;
        int callbackQueryId = 24005;

        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        String wallet = "wallet";
        BigDecimal cryptoAmount = new BigDecimal("0.001");
        String poolDealAmount = "0.001";

        Deal deal = new Deal();
        deal.setWallet(wallet);
        deal.setCryptoCurrency(cryptoCurrency);
        deal.setCryptoAmount(cryptoAmount);

        Message addingToPoolMessage = new Message();
        Integer addingToPoolMessageId = 50001;
        addingToPoolMessage.setMessageId(addingToPoolMessageId);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(responseSender.sendMessage(chatId, "Добавление сделки в пул, пожалуйста подождите.")).thenReturn(Optional.of(addingToPoolMessage));
        when(bigDecimalService.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())).thenReturn(poolDealAmount);
        addToPoolHandler.handle(callbackQuery);
        ArgumentCaptor<PoolDeal> poolDealArgumentCaptor = ArgumentCaptor.forClass(PoolDeal.class);
        verify(cryptoWithdrawalService).addPoolDeal(poolDealArgumentCaptor.capture());
        PoolDeal value = poolDealArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(dealPid, value.getPid()),
                () -> assertEquals(wallet, value.getAddress()),
                () -> assertEquals(botUsername, value.getBot()),
                () -> assertEquals(poolDealAmount, value.getAmount())
        );
        verify(modifyDealService).updateDealStatusByPid(DealStatus.AWAITING_WITHDRAWAL, dealPid);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).deleteMessage(chatId, addingToPoolMessageId);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.001, 1.1, 0.00014, 564})
    void shouldHandlerDifferentAmounts(Double cryptoAmount) {
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User();
        Message message = new Message();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        Long dealPid = 20550L;
        int callbackQueryId = 24005;

        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        String wallet = "wallet";
        BigDecimal cryptoAmountDecimal = new BigDecimal(cryptoAmount);
        String poolDealAmount = "0.001";

        Deal deal = new Deal();
        deal.setWallet(wallet);
        CryptoCurrency cryptoCurrency = CryptoCurrency.MONERO;
        deal.setCryptoCurrency(cryptoCurrency);
        deal.setCryptoAmount(cryptoAmountDecimal);

        Message addingToPoolMessage = new Message();
        Integer addingToPoolMessageId = 50001;
        addingToPoolMessage.setMessageId(addingToPoolMessageId);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(responseSender.sendMessage(chatId, "Добавление сделки в пул, пожалуйста подождите.")).thenReturn(Optional.of(addingToPoolMessage));
        when(bigDecimalService.roundToPlainString(cryptoAmountDecimal, cryptoCurrency.getScale())).thenReturn(poolDealAmount);
        addToPoolHandler.handle(callbackQuery);
        ArgumentCaptor<PoolDeal> poolDealArgumentCaptor = ArgumentCaptor.forClass(PoolDeal.class);
        verify(cryptoWithdrawalService).addPoolDeal(poolDealArgumentCaptor.capture());
        PoolDeal value = poolDealArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(dealPid, value.getPid()),
                () -> assertEquals(wallet, value.getAddress()),
                () -> assertEquals(botUsername, value.getBot()),
                () -> assertEquals(poolDealAmount, value.getAmount())
        );
        verify(modifyDealService).updateDealStatusByPid(DealStatus.AWAITING_WITHDRAWAL, dealPid);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).deleteMessage(chatId, addingToPoolMessageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.ADD_TO_POOL, addToPoolHandler.getCallbackQueryData());
    }
}