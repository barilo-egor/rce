package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Review;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.ReviewService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SHARE_REVIEW)
public class ShareReview extends Processor {

    private final ReviewService reviewService;

    @Autowired
    public ShareReview(IResponseSender responseSender, UserService userService, ReviewService reviewService) {
        super(responseSender, userService);
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
                reviewService.save(Review.builder()
                        .text(UpdateUtil.getMessageText(update))
                        .username(update.getMessage().getFrom().getUserName())
                        .chatId(chatId)
                        .build());
                responseSender.sendMessage(chatId, "Спасибо, ваш отзыв сохранен.");
                userService.getAdminsChatIds().forEach(adminChatId ->
                        responseSender.sendMessage(adminChatId, "Поступил новый отзыв."));
                break;
        }
    }
}
