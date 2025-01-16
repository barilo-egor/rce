package tgb.btc.rce.service.handler.impl.callback.review;

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
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.ReviewPriseType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.impl.process.ReviewPriseProcessService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReviewPrise;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitShareReviewHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IReadDealService readDealService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IModule<ReviewPriseType> reviewPriseModule;

    @Mock
    private VariablePropertiesReader variablePropertiesReader;

    @Mock
    private IReviewService reviewService;

    @Mock
    private INotifyService notifyService;

    @Mock
    private INotificationsAPI notificationsAPI;

    @Mock
    private ReviewPriseProcessService reviewPriseProcessService;

    @InjectMocks
    private SubmitShareReviewHandler submitShareReviewHandler;

    @ParameterizedTest
    @CsvSource({
            "true, very good",
            "true, k",
            "false, very very very very bad!!!"
    })
    void handleWithDynamic(boolean isPublic, String reviewText) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        String firstName = "firstName";
        user.setFirstName(firstName);
        message.setMessageId(messageId);
        message.setText(reviewText);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        Long dealPid = 53444L;
        Deal deal = new Deal();
        BigDecimal dealAmount = new BigDecimal(500);
        deal.setAmount(dealAmount);
        FiatCurrency fiatCurrency = FiatCurrency.BYN;
        deal.setFiatCurrency(fiatCurrency);

        ReviewPrise reviewPrise = new ReviewPrise();
        reviewPrise.setMaxPrise(50);
        reviewPrise.setMinPrise(10);

        Review savedReview = new Review();
        Long savedReviewPid = 100024L;
        savedReview.setPid(savedReviewPid);

        when(callbackDataService.getBoolArgument(data, 1)).thenReturn(isPublic);
        when(callbackDataService.getLongArgument(data, 2)).thenReturn(dealPid);
        when(reviewPriseModule.isCurrent(ReviewPriseType.DYNAMIC)).thenReturn(true);
        when(readDealService.findByPid(dealPid)).thenReturn(deal);
        when(reviewPriseProcessService.getReviewPrise(dealAmount, fiatCurrency)).thenReturn(reviewPrise);
        when(reviewService.save(any())).thenReturn(savedReview);

        submitShareReviewHandler.handle(callbackQuery);
        ArgumentCaptor<Review> reviewArgumentCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewService).save(reviewArgumentCaptor.capture());
        Review review = reviewArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(isPublic ? "Отзыв от firstName:\n\n" + reviewText : "Анонимный отзыв:\n\n" + reviewText, review.getText()),
                () -> assertFalse(review.getPublished()),
                () -> assertEquals(chatId, review.getChatId()),
                () -> assertTrue(review.getAmount() <= 50 && review.getAmount() >= 10)
        );
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Спасибо, ваш отзыв сохранен.");
        verify(notifyService).notifyMessage("Поступил новый отзыв.", Set.of(UserRole.OPERATOR, UserRole.ADMIN));
        verify(notificationsAPI).newReview(savedReview.getPid());
    }

    @ParameterizedTest
    @CsvSource({
            "true, very good",
            "true, k",
            "false, very very very very bad!!!"
    })
    void handleWithStatic(boolean isPublic, String reviewText) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        String firstName = "firstName";
        user.setFirstName(firstName);
        message.setMessageId(messageId);
        message.setText(reviewText);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        Long dealPid = 53444L;
        Deal deal = new Deal();
        BigDecimal dealAmount = new BigDecimal(500);
        deal.setAmount(dealAmount);
        FiatCurrency fiatCurrency = FiatCurrency.BYN;
        deal.setFiatCurrency(fiatCurrency);

        Review savedReview = new Review();
        Long savedReviewPid = 100024L;
        savedReview.setPid(savedReviewPid);
        Integer reviewPriseStatic = 30;

        when(callbackDataService.getBoolArgument(data, 1)).thenReturn(isPublic);
        when(callbackDataService.getLongArgument(data, 2)).thenReturn(dealPid);
        when(reviewPriseModule.isCurrent(ReviewPriseType.DYNAMIC)).thenReturn(false);
        when(variablePropertiesReader.getInt(VariableType.REVIEW_PRISE)).thenReturn(reviewPriseStatic);
        when(reviewService.save(any())).thenReturn(savedReview);

        submitShareReviewHandler.handle(callbackQuery);
        ArgumentCaptor<Review> reviewArgumentCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewService).save(reviewArgumentCaptor.capture());
        Review review = reviewArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(isPublic ? "Отзыв от firstName:\n\n" + reviewText : "Анонимный отзыв:\n\n" + reviewText, review.getText()),
                () -> assertFalse(review.getPublished()),
                () -> assertEquals(chatId, review.getChatId()),
                () -> assertEquals(reviewPriseStatic, review.getAmount())
        );
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Спасибо, ваш отзыв сохранен.");
        verify(notifyService).notifyMessage("Поступил новый отзыв.", Set.of(UserRole.OPERATOR, UserRole.ADMIN));
        verify(notificationsAPI).newReview(savedReview.getPid());
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SUBMIT_SHARE_REVIEW, submitShareReviewHandler.getCallbackQueryData());
    }
}