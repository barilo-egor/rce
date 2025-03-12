package tgb.btc.rce.service.handler.impl.callback.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.process.IReviewProcessService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class PublishReviewHandler implements ICallbackQueryHandler {

    private final IReviewService reviewService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public PublishReviewHandler(IReviewService reviewService, IResponseSender responseSender,
                                ICallbackDataService callbackDataService) {
        this.reviewService = reviewService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();

        Long reviewPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Review review = reviewService.findById(reviewPid);
        review.setIsAccepted(true);
        reviewService.save(review);
        log.debug("Пользователь {} одобрил отзыв {}", chatId, review);

        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв одобрен.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PUBLISH_REVIEW;
    }
}
