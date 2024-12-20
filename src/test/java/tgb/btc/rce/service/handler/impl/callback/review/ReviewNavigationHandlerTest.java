package tgb.btc.rce.service.handler.impl.callback.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.process.IReviewProcessService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewNavigationHandlerTest {

    @Mock
    private IReviewService reviewService;

    @Mock
    private IReviewProcessService reviewProcessService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private ReviewNavigationHandler reviewNavigationHandler;

    @Test
    @DisplayName("Должен отправить следующие отзывы.")
    void handleWithReviews() {
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

        Long lastPid = 23334L;

        Review review = new Review();
        review.setPid(23335L);
        List<Review> reviews = List.of(review);
        String newData = "newData";

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(lastPid);
        when(reviewService.findMoreThanPid(lastPid, 5)).thenReturn(reviews);
        when(callbackDataService.buildData(CallbackQueryData.REVIEW_NAVIGATION, 23335L)).thenReturn(newData);

        reviewNavigationHandler.handle(callbackQuery);

        verify(responseSender, times(0)).sendMessage(chatId, "Больше отзывов нет.");
        verify(reviewProcessService).sendNewReviews(chatId, reviews);
        verify(callbackDataService).buildData(CallbackQueryData.REVIEW_NAVIGATION, 23335L);
        verify(responseSender).sendMessage(chatId, "Навигация по отзывам.", InlineButton.builder().text("Следующие 5").data(newData).build());
    }

    @Test
    @DisplayName("Должен сообщить, что отзывов больше нет..")
    void handleWithoutReviews() {
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

        Long lastPid = 23334L;

        List<Review> reviews = new ArrayList<>();

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(lastPid);
        when(reviewService.findMoreThanPid(lastPid, 5)).thenReturn(reviews);

        reviewNavigationHandler.handle(callbackQuery);

        verify(responseSender).sendMessage(chatId, "Больше отзывов нет.");
        verify(reviewProcessService, times(0)).sendNewReviews(anyLong(), anyList());
        verify(callbackDataService, times(0)).buildData(any(), anyLong());
        verify(responseSender, times(0)).sendMessage(anyLong(), anyString(), any(InlineButton.class));
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.REVIEW_NAVIGATION, reviewNavigationHandler.getCallbackQueryData());
    }

}