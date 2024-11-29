package tgb.btc.rce.service.handler.impl.callback.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.process.IReviewProcessService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class PublishReviewHandler implements ICallbackQueryHandler {

    private final IReviewProcessService reviewProcessService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public PublishReviewHandler(IReviewProcessService reviewProcessService, IResponseSender responseSender,
                                ICallbackDataService callbackDataService) {
        this.reviewProcessService = reviewProcessService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();

        Long reviewPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        reviewProcessService.publish(reviewPid);
        log.debug("Админ {} опубликовал отзыв {}", chatId, reviewPid);

        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Отзыв опубликован.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PUBLISH_REVIEW;
    }
}
