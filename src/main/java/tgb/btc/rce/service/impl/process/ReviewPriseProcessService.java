package tgb.btc.rce.service.impl.process;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.library.IReviewPriseProcessService;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.PropertyValueNotFoundException;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReviewPriseType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.service.util.IReviewPriseBotProcessService;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReviewPrise;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewPriseProcessService implements IReviewPriseProcessService, IReviewPriseBotProcessService {

    private final List<ReviewPrise> reviewPrises = new ArrayList<>();

    private IResponseSender responseSender;

    private IReadDealService readDealService;

    private IKeyboardBuildService keyboardBuildService;

    private ICallbackQueryService callbackQueryService;

    private VariablePropertiesReader variablePropertiesReader;

    private ICommandService commandService;

    private IModule<ReferralType> referralModule;

    private IModule<ReviewPriseType> reviewPriseModule;

    @Autowired
    public void setReferralModule(IModule<ReferralType> referralModule) {
        this.referralModule = referralModule;
    }

    @Autowired
    public void setReviewPriseModule(IModule<ReviewPriseType> reviewPriseModule) {
        this.reviewPriseModule = reviewPriseModule;
    }

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setCallbackQueryService(ICallbackQueryService callbackQueryService) {
        this.callbackQueryService = callbackQueryService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void processReviewPrise(Long dealPid) {
        Deal deal = readDealService.findByPid(dealPid);
        if (referralModule.isCurrent(ReferralType.STANDARD)) {
            String data;
            String amount;
            if (reviewPriseModule.isCurrent(ReviewPriseType.DYNAMIC)) {
                ReviewPrise reviewPriseVo = getReviewPrise(deal.getAmount(), deal.getFiatCurrency());
                if (Objects.nonNull(reviewPriseVo)) {
                    data = callbackQueryService.buildCallbackData(Command.SHARE_REVIEW,
                            new Object[]{String.valueOf(reviewPriseVo.getMinPrise()), String.valueOf(reviewPriseVo.getMaxPrise())});
                    amount = "от " + reviewPriseVo.getMinPrise() + "₽" + " до " + reviewPriseVo.getMaxPrise() + "₽";
                }
                else return;
            } else {
                data = Command.SHARE_REVIEW.name();
                amount = variablePropertiesReader.getInt(VariableType.REVIEW_PRISE) +"₽";
            }
            responseSender.sendMessage(deal.getUser().getChatId(), "Хотите оставить отзыв?\n" +
                            "За оставленный отзыв вы получите вознаграждение в размере " + amount +
                            " на реферальный баланс после публикации.",
                    keyboardBuildService.buildInline(List.of(
                                    InlineButton.builder()
                                            .data(data)
                                            .text(commandService.getText(Command.SHARE_REVIEW))
                                            .build()
                            )
                    ));
        }
    }

    @Override
    public ReviewPrise getReviewPrise(BigDecimal sum, FiatCurrency fiatCurrency) {
        for (ReviewPrise reviewPrise : reviewPrises.stream()
                .filter(reviewPrise -> reviewPrise.getFiatCurrency().equals(fiatCurrency))
                .collect(Collectors.toList())) {
            if (BigDecimal.valueOf(reviewPrise.getSum()).compareTo(sum) < 1)
                return reviewPrise;
        }
        return null;
    }

    @PostConstruct
    private void load() {
        reviewPrises.clear();
        for (String key : PropertiesPath.REVIEW_PRISE_PROPERTIES.getKeys()) {
            int sum;
            if (StringUtils.isBlank(key)) {
                throw new PropertyValueNotFoundException("Не указано название для одного из ключей" + key + ".");
            }
            try {
                sum = Integer.parseInt(key.split("\\.")[1]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное название для ключа " + key + ".");
            }
            String[] priseValues = PropertiesPath.REVIEW_PRISE_PROPERTIES.getStringArray(key);
            if (priseValues.length == 0) {
                throw new PropertyValueNotFoundException("Не указано значение для ключа " + key + ".");
            }
            int minPrise;
            int maxPrise;
            try {
                minPrise = Integer.parseInt(priseValues[0]);
                maxPrise = Integer.parseInt(priseValues[1]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное значение для ключа " + key + ".");
            }
            reviewPrises.add(ReviewPrise.builder()
                    .minPrise(minPrise)
                    .maxPrise(maxPrise)
                    .sum(sum)
                    .fiatCurrency(FiatCurrency.getByCode(key.split("\\.")[0]))
                    .build());
        }
        reviewPrises.sort(Comparator.comparingInt(ReviewPrise::getSum));
        Collections.reverse(reviewPrises);
    }
}
