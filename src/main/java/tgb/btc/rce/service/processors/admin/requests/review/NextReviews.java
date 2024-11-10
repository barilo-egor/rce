package tgb.btc.rce.service.processors.admin.requests.review;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.process.IReviewProcessService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.REVIEW_NAVIGATION)
public class NextReviews extends Processor {

    private final IReviewService reviewService;

    private final IReviewProcessService reviewProcessService;

    public NextReviews(IReviewService reviewService, IReviewProcessService reviewProcessService) {
        this.reviewService = reviewService;
        this.reviewProcessService = reviewProcessService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long lastPid = callbackQueryService.getSplitLongData(update, 1);

        List<Review> reviews = reviewService.findMoreThanPid(lastPid, 5);
        if (reviews.isEmpty()) {
            responseSender.sendMessage(chatId, "Больше отзывов нет.");
            return;
        }
        reviewProcessService.sendNewReviews(chatId, reviews);
        responseSender.sendMessage(chatId, "Навигация по отзывам.",
                InlineButton.builder()
                        .text("Следующие 5")
                        .data(callbackQueryService.buildCallbackData(
                                Command.REVIEW_NAVIGATION,
                                reviews.get(reviews.size() - 1).getPid())
                        ).build()
        );
    }
}
