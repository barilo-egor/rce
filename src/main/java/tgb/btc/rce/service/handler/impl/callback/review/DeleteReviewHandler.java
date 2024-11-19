package tgb.btc.rce.service.handler.impl.callback.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class DeleteReviewHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IReviewService reviewService;

    public DeleteReviewHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                               IReviewService reviewService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.reviewService = reviewService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Review review = reviewService.findById(callbackDataService.getLongArgument(callbackQuery.getData(), 1));
        String reviewToString = review.toString();
        reviewService.deleteById(review.getPid());
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв удален.");
        log.debug("Админ {} удалил отзыв: {}", chatId, reviewToString);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETE_REVIEW;
    }
}
