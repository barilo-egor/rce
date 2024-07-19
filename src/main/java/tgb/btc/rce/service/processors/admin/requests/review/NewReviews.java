package tgb.btc.rce.service.processors.admin.requests.review;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.NEW_REVIEWS)
public class NewReviews extends Processor {

    private IReviewService reviewService;

    @Autowired
    public void setReviewService(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        List<Review> reviews = reviewService.findAllByIsPublished(false);
        if (reviews.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых отзывов нет.");
            return;
        }

        for (Review review : reviews) {
            List<InlineButton> buttons = new ArrayList<>();

            buttons.add(InlineButton.builder()
                    .text("Опубликовать")
                    .data(Command.PUBLISH_REVIEW.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + review.getPid())
                    .build());
            buttons.add(InlineButton.builder()
                    .text("Удалить")
                    .data(Command.DELETE_REVIEW.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + review.getPid())
                    .build());

            responseSender.sendMessage(chatId, review.getText()
                    + "\nUsername: " + StringUtils.defaultIfEmpty(review.getUsername(), "скрыт") + "\n"
                    + "ID: " + review.getChatId(), keyboardBuildService.buildInline(buttons));
        }
    }
}
