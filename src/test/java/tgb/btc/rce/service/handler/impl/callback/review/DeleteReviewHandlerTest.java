package tgb.btc.rce.service.handler.impl.callback.review;

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
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteReviewHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IReviewService reviewService;

    @InjectMocks
    private DeleteReviewHandler deleteReviewHandler;

    @Test
    void handle() {
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

        Long reviewPid = 23345L;
        Review review = new Review();
        review.setPid(reviewPid);

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(reviewPid);
        when(reviewService.findById(reviewPid)).thenReturn(review);

        deleteReviewHandler.handle(callbackQuery);

        verify(reviewService).deleteById(reviewPid);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Отзыв удален.");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DELETE_REVIEW, deleteReviewHandler.getCallbackQueryData());
    }
}