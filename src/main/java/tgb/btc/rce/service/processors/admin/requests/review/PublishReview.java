package tgb.btc.rce.service.processors.admin.requests.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.process.IReviewProcessService;

@CommandProcessor(command = Command.PUBLISH_REVIEW)
@Slf4j
public class PublishReview extends Processor {

    private final IReviewProcessService reviewProcessService;

    @Autowired
    public PublishReview(IReviewProcessService reviewProcessService) {
        this.reviewProcessService = reviewProcessService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);

        Long reviewPid = Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        reviewProcessService.publish(reviewPid);
        log.debug("Админ {} опубликовал отзыв {}", chatId, reviewPid);

        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв опубликован.");
    }
}
