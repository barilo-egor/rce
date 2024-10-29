package tgb.btc.rce.service.processors.admin.requests.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.DELETE_REVIEW)
@Slf4j
public class DeleteReview extends Processor {

    private IReviewService reviewService;

    @Autowired
    public void setReviewService(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Review review = reviewService.findById(Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));
        String reviewToString = review.toString();
        reviewService.deleteById(review.getPid());
        Long chatId = updateService.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв удален.");
        log.debug("Админ {} удалил отзыв: {}", chatId, reviewToString);
    }
}
