package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tgb.btc.api.bot.ReviewAPI;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.rce.service.process.IReviewProcessService;

import java.util.List;

@Service
public class ReviewAPIImpl implements ReviewAPI {

    private final IReviewProcessService reviewProcessService;

    private final INotificationsAPI notificationsAPI;

    @Autowired
    public ReviewAPIImpl(IReviewProcessService reviewProcessService, INotificationsAPI notificationsAPI) {
        this.reviewProcessService = reviewProcessService;
        this.notificationsAPI = notificationsAPI;
    }

    @Override
    public void publishReview(Long aLong) {
        reviewProcessService.publish(aLong);
        notificationsAPI.reviewPublished();
    }

    @Override
    @Async
    public void publishReview(List<Long> list) {
        for (Long pid : list) {
            reviewProcessService.publish(pid);
            notificationsAPI.reviewPublished();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        notificationsAPI.publicationOfReviewsOver();
    }
}
