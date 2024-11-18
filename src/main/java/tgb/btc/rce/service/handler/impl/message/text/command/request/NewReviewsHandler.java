package tgb.btc.rce.service.handler.impl.message.text.command.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.process.IReviewProcessService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class NewReviewsHandler implements ITextCommandHandler {

    private final IReviewService reviewService;

    private final IResponseSender responseSender;

    private final IReviewProcessService reviewProcessService;

    private final ICallbackQueryService callbackQueryService;

    public NewReviewsHandler(IReviewService reviewService, IResponseSender responseSender,
                             IReviewProcessService reviewProcessService, ICallbackQueryService callbackQueryService) {
        this.reviewService = reviewService;
        this.responseSender = responseSender;
        this.reviewProcessService = reviewProcessService;
        this.callbackQueryService = callbackQueryService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        List<Review> reviews = reviewService.findAllByIsPublished(false, 0, 5);
        if (reviews.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых отзывов нет.");
            return;
        }
        reviewProcessService.sendNewReviews(chatId, reviews);
        responseSender.sendMessage(chatId, "Навигация по отзывам.",
                InlineButton.builder()
                        .text("Следующие 5")
                        .data(callbackQueryService.buildCallbackData(
                                Command.REVIEW_NAVIGATION,
                                reviews.get(reviews.size() - 1).getPid()))
                        .build()
        );
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.NEW_REVIEWS;
    }
}
