package tgb.btc.rce.service.processors.deal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.ReviewPriseType;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReviewPrise;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static tgb.btc.rce.enums.ReviewPriseType.DYNAMIC;

@CommandProcessor(command = Command.SHARE_REVIEW)
public class ShareReview extends Processor {

    private IReviewService reviewService;

    private INotifyService notifyService;

    private VariablePropertiesReader variablePropertiesReader;

    private IModule<ReviewPriseType> reviewPriseModule;

    private final INotificationsAPI notificationsAPI;

    @Autowired
    public ShareReview(INotificationsAPI notificationsAPI) {
        this.notificationsAPI = notificationsAPI;
    }

    @Autowired
    public void setReviewPriseModule(IModule<ReviewPriseType> reviewPriseModule) {
        this.reviewPriseModule = reviewPriseModule;
    }

    @Autowired
    public void setNotifyService(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    public static Map<Long, ReviewPrise> reviewPrisesMap = new ConcurrentHashMap<>();

    @Autowired
    public void setReviewService(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (checkForCancel(update)) {
            return;
        }
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                responseSender.sendMessage(chatId, "Напишите ваш отзыв.");
                modifyUserService.nextStep(chatId, Command.SHARE_REVIEW.name());
                if (reviewPriseModule.isCurrent(DYNAMIC)) reviewPrisesMap.put(chatId, new ReviewPrise(update.getCallbackQuery().getData()));
                return;
            case 1:
                if (update.hasMessage() && StringUtils.isNotEmpty(update.getMessage().getFrom().getUserName())) {
                    modifyUserService.updateBufferVariable(chatId, updateService.getMessageText(update));
                    responseSender.sendMessage(chatId, "Оставить отзыв публично или анонимно?",
                            keyboardBuildService.buildInline(List.of(InlineButton.builder()
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
                Integer amount = reviewPriseModule.isCurrent(DYNAMIC)
                                 ? getRandomAmount(chatId)
                                 : variablePropertiesReader.getInt(VariableType.REVIEW_PRISE);
                Review review;
                if (update.hasMessage()) {
                    review = reviewService.save(Review.builder()
                            .text(author + updateService.getMessageText(update))
                            .username(update.getMessage().getFrom().getFirstName())
                            .isPublished(false)
                            .chatId(chatId)
                            .amount(amount)
                            .build());
                } else {
                    if (update.getCallbackQuery().getData().equals("public"))
                        author = "Отзыв от " + update.getCallbackQuery().getFrom().getFirstName() + "\n\n";
                    review = reviewService.save(Review.builder()
                            .text(author + readUserService.getBufferVariable(chatId))
                            .username(update.getCallbackQuery().getFrom().getFirstName())
                            .isPublished(false)
                            .chatId(chatId)
                            .amount(amount)
                            .build());
                }
                responseSender.sendMessage(chatId, "Спасибо, ваш отзыв сохранен.");
                notifyService.notifyMessage("Поступил новый отзыв.", Set.of(UserRole.OPERATOR, UserRole.ADMIN));
                notificationsAPI.newReview(review.getPid());
                processToMainMenu(chatId);
                break;
        }
    }

    private int getRandomAmount(Long chatId) {
        ReviewPrise reviewPrise = reviewPrisesMap.get(chatId);
        return (int) (Math.random() * (reviewPrise.getMaxPrise() - reviewPrise.getMinPrise()) + reviewPrise.getMinPrise() + 0.5);
    }

}
