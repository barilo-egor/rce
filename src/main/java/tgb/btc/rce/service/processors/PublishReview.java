package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Review;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.bean.ReviewService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.PUBLISH_REVIEW)
@Slf4j
public class PublishReview extends Processor {

    private ReviewService reviewService;

    @Autowired
    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long channelChatId = Long.parseLong(BotVariablePropertiesUtil.getVariable(BotVariableType.CHANNEL_CHAT_ID));

        Review review = reviewService.findById(Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));

//        String username = StringUtils.isEmpty(review.getUsername()) ? StringUtils.EMPTY
//                : "\nОтзыв от @" + review.getUsername();
        responseSender.sendMessage(channelChatId, review.getText());
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв опубликован.");
        review.setPublished(true);
        reviewService.save(review);
        Integer reviewPrise = BotVariablePropertiesUtil.getInt(BotVariableType.REVIEW_PRISE);
        Integer referralBalance = userService.getReferralBalanceByChatId(review.getChatId());
        int total = referralBalance + reviewPrise;
        log.info("Обновление реф баланса за отзыв : chatId = " + review.getChatId() + "; reviewPrise = "
                + reviewPrise + "; referralBalance = " + referralBalance + "; total = " + total);
        userService.updateReferralBalanceByChatId(total, review.getChatId());
        responseSender.sendMessage(review.getChatId(), "Ваш отзыв опубликован.\n\nНа ваш реферальный баланс зачислено "
                + reviewPrise + "₽.");
    }
}
