package tgb.btc.rce.service.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.library.IReviewPriseService;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.util.properties.VariablePropertiesUtil;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReviewPriseType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.ReviewPriseUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReviewPrise;

import java.util.List;
import java.util.Objects;

@Service
public class ReviewPriseService implements IReviewPriseService {

    private IResponseSender responseSender;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void processReviewPrise(Long dealPid) {
        Deal deal = dealRepository.getById(dealPid);
        if (ReferralType.STANDARD.isCurrent()) {
            String data;
            String amount;
            if (ReviewPriseType.DYNAMIC.isCurrent()) {
                ReviewPrise reviewPriseVo = ReviewPriseUtil.getReviewPrise(deal.getAmount(), deal.getFiatCurrency());
                if (Objects.nonNull(reviewPriseVo)) {
                    data = CallbackQueryUtil.buildCallbackData(Command.SHARE_REVIEW,
                            String.valueOf(reviewPriseVo.getMinPrise()), String.valueOf(reviewPriseVo.getMaxPrise()));
                    amount = "от " + reviewPriseVo.getMinPrise() + "₽" + " до " + reviewPriseVo.getMaxPrise() + "₽";
                }
                else return;
            } else {
                data = Command.SHARE_REVIEW.name();
                amount = VariablePropertiesUtil.getInt(VariableType.REVIEW_PRISE) +"₽";
            }
            responseSender.sendMessage(deal.getUser().getChatId(), "Хотите оставить отзыв?\n" +
                            "За оставленный отзыв вы получите вознаграждение в размере " + amount +
                            " на реферальный баланс после публикации.",
                    KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .data(data)
                                            .text("Оставить")
                                            .build()
                            )
                    ));
        }
    }
}
