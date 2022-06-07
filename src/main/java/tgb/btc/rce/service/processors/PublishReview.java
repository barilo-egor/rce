package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Review;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.ReviewService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.PUBLISH_REVIEW)
public class PublishReview extends Processor {

    private final ReviewService reviewService;

    @Autowired
    public PublishReview(IResponseSender responseSender, UserService userService, ReviewService reviewService) {
        super(responseSender, userService);
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long channelChatId = Long.parseLong(BotVariablePropertiesUtil.getVariable(BotVariableType.CHANNEL_CHAT_ID));

        Review review = reviewService.findById(Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));

        String username = StringUtils.isEmpty(review.getUsername()) ? StringUtils.EMPTY
                : "\nОтзыв от @" + review.getUsername();
        responseSender.sendMessage(channelChatId, review.getText() + username);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв опубликован.");
    }
}
