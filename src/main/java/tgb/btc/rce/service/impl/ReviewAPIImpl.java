package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.bot.ReviewAPI;
import tgb.btc.rce.service.process.IReviewProcessService;

@Service
public class ReviewAPIImpl implements ReviewAPI {

    private IReviewProcessService reviewProcessService;

    @Autowired
    public ReviewAPIImpl(IReviewProcessService reviewProcessService) {
        this.reviewProcessService = reviewProcessService;
    }

    @Override
    public void publishReview(Long aLong) {
        reviewProcessService.publish(aLong);
    }
}
