package tgb.btc.lib.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.Review;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.ReviewService;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.NEW_REVIEWS)
public class NewReviews extends Processor {

    private ReviewService reviewService;

    @Autowired
    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<Review> reviews = reviewService.findAllByIsPublished(false);
        if (reviews.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых отзывов нет.");
            return;
        }

        for (Review review : reviews) {
            List<InlineButton> buttons = new ArrayList<>();

            buttons.add(InlineButton.builder()
                    .text("Опубликовать")
                    .data(Command.PUBLISH_REVIEW.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + review.getPid())
                    .build());
            buttons.add(InlineButton.builder()
                    .text("Удалить")
                    .data(Command.DELETE_REVIEW.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + review.getPid())
                    .build());

            responseSender.sendMessage(chatId, review.getText()
                    + "\nUsername: " + StringUtils.defaultIfEmpty(review.getUsername(), "скрыт") + "\n"
                    + "ID: " + review.getChatId(), KeyboardUtil.buildInline(buttons));
        }
    }
}
