package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.service.bean.bot.ReviewService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReviewPrise;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static tgb.btc.rce.enums.ReviewPriseType.DYNAMIC;

@CommandProcessor(command = Command.SHARE_REVIEW)
public class ShareReview extends Processor {

    private ReviewService reviewService;

    public static Map<Long, ReviewPrise> reviewPrisesMap = new ConcurrentHashMap<>();

    @Autowired
    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) {
            return;
        }
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                responseSender.sendMessage(chatId, "Напишите ваш отзыв.");
                userRepository.nextStep(chatId, Command.SHARE_REVIEW.name());
                if (DYNAMIC.isCurrent()) reviewPrisesMap.put(chatId, new ReviewPrise(update.getCallbackQuery().getData()));
                return;
            case 1:
                if (update.hasMessage() && StringUtils.isNotEmpty(update.getMessage().getFrom().getUserName())) {
                    userService.updateBufferVariable(chatId, UpdateUtil.getMessageText(update));
                    responseSender.sendMessage(chatId, "Оставить отзыв публично или анонимно?",
                            KeyboardUtil.buildInline(List.of(InlineButton.builder()
                                            .inlineType(InlineType.CALLBACK_DATA)
                                            .text("Публично")
                                            .data("public")
                                            .build(),
                                    InlineButton.builder()
                                            .inlineType(InlineType.CALLBACK_DATA)
                                            .text("Анонимно")
                                            .data("anonym")
                                            .build())));
                    return;
                }
            case 2:
                String author = "Анонимный отзыв\n\n";
                Integer amount = DYNAMIC.isCurrent()
                                 ? getRandomAmount(chatId)
                                 : BotVariablePropertiesUtil.getInt(VariableType.REVIEW_PRISE);
                if (update.hasMessage()) {
                    reviewService.save(Review.builder()
                            .text(author + UpdateUtil.getMessageText(update))
                            .username(update.getMessage().getFrom().getFirstName())
                            .isPublished(false)
                            .chatId(chatId)
                            .amount(amount)
                            .build());
                } else if (update.hasCallbackQuery()) {
                    if (update.getCallbackQuery().getData().equals("public"))
                        author = "Отзыв от " + update.getCallbackQuery().getFrom().getFirstName() + "\n\n";
                    reviewService.save(Review.builder()
                            .text(author + userService.getBufferVariable(chatId))
                            .username(update.getCallbackQuery().getFrom().getFirstName())
                            .isPublished(false)
                            .chatId(chatId)
                            .amount(amount)
                            .build());
                }
                responseSender.sendMessage(chatId, "Спасибо, ваш отзыв сохранен.");
                userService.getAdminsChatIds().forEach(adminChatId ->
                        responseSender.sendMessage(adminChatId, "Поступил новый отзыв."));
                processToMainMenu(chatId);
                break;
        }
    }

    private int getRandomAmount(Long chatId) {
        ReviewPrise reviewPrise = reviewPrisesMap.get(chatId);
        return (int) (Math.random() * (reviewPrise.getMaxPrise() - reviewPrise.getMinPrise()) + reviewPrise.getMinPrise() + 0.5);
    }

}
