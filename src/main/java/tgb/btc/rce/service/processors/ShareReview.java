package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Review;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.ReviewService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.SHARE_REVIEW)
public class ShareReview extends Processor {

    private ReviewService reviewService;

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
                userService.nextStep(chatId, Command.SHARE_REVIEW);
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
                if (update.hasMessage()) {
                    reviewService.save(Review.builder()
                            .text(author + UpdateUtil.getMessageText(update))
                            .username(update.getMessage().getFrom().getFirstName())
                                    .isPublished(false)
                            .chatId(chatId)
                            .build());
                } else if (update.hasCallbackQuery()) {
                    if (update.getCallbackQuery().getData().equals("public"))
                        author = "Отзыв от " + update.getCallbackQuery().getFrom().getFirstName() + "\n\n";
                    reviewService.save(Review.builder()
                            .text(author + userService.getBufferVariable(chatId))
                            .username(update.getCallbackQuery().getFrom().getFirstName())
                                    .isPublished(false)
                            .chatId(chatId)
                            .build());
                }
                responseSender.sendMessage(chatId, "Спасибо, ваш отзыв сохранен.");
                userService.getAdminsChatIds().forEach(adminChatId ->
                        responseSender.sendMessage(adminChatId, "Поступил новый отзыв."));
                processToMainMenu(chatId);
                break;
        }
    }
}
