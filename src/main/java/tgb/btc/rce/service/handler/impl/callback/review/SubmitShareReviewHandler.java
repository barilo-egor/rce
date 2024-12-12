package tgb.btc.rce.service.handler.impl.callback.review;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.ReviewPriseType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.impl.process.ReviewPriseProcessService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReviewPrise;

import java.util.Objects;
import java.util.Set;

import static tgb.btc.rce.enums.ReviewPriseType.DYNAMIC;

@Service
public class SubmitShareReviewHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IReadDealService readDealService;

    private final ICallbackDataService callbackDataService;

    private final IModule<ReviewPriseType> reviewPriseModule;

    private final VariablePropertiesReader variablePropertiesReader;

    private final IReviewService reviewService;

    private final INotifyService notifyService;

    private final INotificationsAPI notificationsAPI;

    private final ReviewPriseProcessService reviewPriseProcessService;

    public SubmitShareReviewHandler(IResponseSender responseSender, IReadDealService readDealService,
                                    ICallbackDataService callbackDataService, IModule<ReviewPriseType> reviewPriseModule,
                                    VariablePropertiesReader variablePropertiesReader, IReviewService reviewService,
                                    INotifyService notifyService, INotificationsAPI notificationsAPI,
                                    ReviewPriseProcessService reviewPriseProcessService) {
        this.responseSender = responseSender;
        this.readDealService = readDealService;
        this.callbackDataService = callbackDataService;
        this.reviewPriseModule = reviewPriseModule;
        this.variablePropertiesReader = variablePropertiesReader;
        this.reviewService = reviewService;
        this.notifyService = notifyService;
        this.notificationsAPI = notificationsAPI;
        this.reviewPriseProcessService = reviewPriseProcessService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Boolean isPublic = callbackDataService.getBoolArgument(callbackQuery.getData(), 1);
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 2);
        String text = callbackQuery.getMessage().getText();
        Integer amount = reviewPriseModule.isCurrent(DYNAMIC)
                ? getRandomAmount(dealPid)
                : variablePropertiesReader.getInt(VariableType.REVIEW_PRISE);
        if (Boolean.TRUE.equals(isPublic)) {
            text = "Анонимный отзыв:\n\n" + text;
        } else {
            text = "Отзыв от " +  callbackQuery.getFrom().getFirstName() + ":\n\n" + text;
        }
        Review review = reviewService.save(Review.builder()
                .text(text)
                .isPublished(false)
                .chatId(chatId)
                .amount(amount)
                .build());
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Спасибо, ваш отзыв сохранен.");
        notifyService.notifyMessage("Поступил новый отзыв.", Set.of(UserRole.OPERATOR, UserRole.ADMIN));
        notificationsAPI.newReview(review.getPid());
    }

    private Integer getRandomAmount(Long dealPid) {
        Deal deal = readDealService.findByPid(dealPid);
        ReviewPrise reviewPrise = reviewPriseProcessService.getReviewPrise(deal.getAmount(), deal.getFiatCurrency());
        if (Objects.isNull(reviewPrise)) {
            return null;
        }
        return (int) (Math.random() * (reviewPrise.getMaxPrise() - reviewPrise.getMinPrise()) + reviewPrise.getMinPrise() + 0.5);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SUBMIT_SHARE_REVIEW;
    }
}
