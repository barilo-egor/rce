package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Review;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.ReviewService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.NEW_REVIEWS)
public class NewReviews extends Processor {

    private final ReviewService reviewService;

    @Autowired
    public NewReviews(IResponseSender responseSender, UserService userService, ReviewService reviewService) {
        super(responseSender, userService);
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<Review> reviews = reviewService.findAll();

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
                    + "ID: " + review.getChatId());
        }
    }
}
