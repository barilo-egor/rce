package tgb.btc.rce.service.processors.admin.requests.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReviewPriseType;
import tgb.btc.rce.service.Processor;

import static tgb.btc.rce.enums.ReviewPriseType.DYNAMIC;

@CommandProcessor(command = Command.PUBLISH_REVIEW)
@Slf4j
public class PublishReview extends Processor {

    private IReviewService reviewService;

    private VariablePropertiesReader variablePropertiesReader;

    private IModule<ReviewPriseType> reviewPriseModule;

    @Autowired
    public void setReviewPriseModule(IModule<ReviewPriseType> reviewPriseModule) {
        this.reviewPriseModule = reviewPriseModule;
    }

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setReviewService(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long channelChatId = Long.parseLong(variablePropertiesReader.getVariable(VariableType.CHANNEL_CHAT_ID));

        Review review = reviewService.findById(Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));

//        String username = StringUtils.isEmpty(review.getUsername()) ? StringUtils.EMPTY
//                : "\nОтзыв от @" + review.getUsername();
        responseSender.sendMessage(channelChatId, review.getText());
        Long chatId = updateService.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв опубликован.");
        review.setPublished(true);
        reviewService.save(review);
        Integer reviewPrise = reviewPriseModule.isCurrent(DYNAMIC)
                ? review.getAmount()
                : variablePropertiesReader.getInt(VariableType.REVIEW_PRISE);
        Integer referralBalance = readUserService.getReferralBalanceByChatId(review.getChatId());
        int total = referralBalance + reviewPrise;
        log.info("Обновление реф баланса за отзыв : chatId = " + review.getChatId() + "; reviewPrise = "
                + reviewPrise + "; referralBalance = " + referralBalance + "; total = " + total);
        modifyUserService.updateReferralBalanceByChatId(total, review.getChatId());
        responseSender.sendMessage(review.getChatId(), "Ваш отзыв опубликован.\n\nНа ваш реферальный баланс зачислено "
                + reviewPrise + "₽.");
    }
}
