package tgb.btc.rce.service.impl.process;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.Review;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.IReviewService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReviewPriseType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.process.IReviewProcessService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tgb.btc.rce.enums.ReviewPriseType.DYNAMIC;

@Service
@Slf4j
public class ReviewProcessService implements IReviewProcessService {

    private final VariablePropertiesReader variablePropertiesReader;

    private final IReviewService reviewService;

    private final IResponseSender responseSender;

    private final IModule<ReviewPriseType> reviewPriseModule;

    private final IReadUserService readUserService;

    private final IModifyUserService modifyUserService;

    private final IKeyboardBuildService keyboardBuildService;

    private final ICommandService commandService;

    @Autowired
    public ReviewProcessService(VariablePropertiesReader variablePropertiesReader, IReviewService reviewService,
                                IResponseSender responseSender, IModule<ReviewPriseType> reviewPriseModule,
                                IReadUserService readUserService, IModifyUserService modifyUserService,
                                IKeyboardBuildService keyboardBuildService, ICommandService commandService) {
        this.variablePropertiesReader = variablePropertiesReader;
        this.reviewService = reviewService;
        this.responseSender = responseSender;
        this.reviewPriseModule = reviewPriseModule;
        this.readUserService = readUserService;
        this.modifyUserService = modifyUserService;
        this.keyboardBuildService = keyboardBuildService;
        this.commandService = commandService;
    }

    @Override
    public void publish(Long pid) {
        Long channelChatId = Long.parseLong(variablePropertiesReader.getVariable(VariableType.CHANNEL_CHAT_ID));
        Review review = reviewService.findById(pid);
        responseSender.sendMessage(channelChatId, review.getText());
        review.setPublished(true);
        reviewService.save(review);
        Integer reviewPrise = reviewPriseModule.isCurrent(DYNAMIC) && Objects.nonNull(review.getAmount())
                ? review.getAmount()
                : variablePropertiesReader.getInt(VariableType.REVIEW_PRISE);
        Integer referralBalance = readUserService.getReferralBalanceByChatId(review.getChatId());
        int total = referralBalance + reviewPrise;

        log.debug("Обновление реф баланса за отзыв : chatId = " + review.getChatId() + "; reviewPrise = "
                + reviewPrise + "; referralBalance = " + referralBalance + "; total = " + total);
        modifyUserService.updateReferralBalanceByChatId(total, review.getChatId());
        responseSender.sendMessage(review.getChatId(), "Ваш отзыв опубликован.\n\nНа ваш реферальный баланс зачислено "
                + reviewPrise + "₽.");
    }

    @Override
    public void sendNewReviews(Long chatId, List<Review> reviews) {
        for (Review review : reviews) {
            List<InlineButton> buttons = new ArrayList<>();

            buttons.add(InlineButton.builder()
                    .text(commandService.getText(Command.PUBLISH_REVIEW))
                    .data(Command.PUBLISH_REVIEW.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + review.getPid())
                    .build());
            buttons.add(InlineButton.builder()
                    .text(commandService.getText(Command.DELETE_REVIEW))
                    .data(Command.DELETE_REVIEW.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + review.getPid())
                    .build());

            responseSender.sendMessage(chatId, review.getText()
                    + "\nUsername: " + StringUtils.defaultIfEmpty(review.getUsername(), "скрыт") + "\n"
                    + "ID: " + review.getChatId(), keyboardBuildService.buildInline(buttons));
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
