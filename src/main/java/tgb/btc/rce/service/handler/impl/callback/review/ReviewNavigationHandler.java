package tgb.btc.rce.service.handler.impl.callback.review;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.process.IReviewProcessService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class ReviewNavigationHandler implements ICallbackQueryHandler {

    private final IReviewService reviewService;

    private final IReviewProcessService reviewProcessService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    public ReviewNavigationHandler(IReviewService reviewService, IReviewProcessService reviewProcessService,
                                   ICallbackDataService callbackDataService, IResponseSender responseSender) {
        this.reviewService = reviewService;
        this.reviewProcessService = reviewProcessService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long lastPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);

        List<Review> reviews = reviewService.findMoreThanPid(lastPid, 5);
        if (reviews.isEmpty()) {
            responseSender.sendMessage(chatId, "Больше отзывов нет.");
            return;
        }
        reviewProcessService.sendNewReviews(chatId, reviews);
        responseSender.sendMessage(chatId, "Навигация по отзывам.",
                InlineButton.builder()
                        .text("Следующие 5")
                        .data(callbackDataService.buildData(
                                CallbackQueryData.REVIEW_NAVIGATION,
                                reviews.get(reviews.size() - 1).getPid())
                        ).build()
        );
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.REVIEW_NAVIGATION;
    }
}
