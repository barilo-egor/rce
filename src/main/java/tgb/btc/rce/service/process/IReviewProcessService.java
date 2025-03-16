package tgb.btc.rce.service.process;

import tgb.btc.library.bean.bot.Review;

import java.util.List;

public interface IReviewProcessService {
    void publish(Long pid);

    void sendNewReviews(Long chatId, List<Review> reviews);

    void publishNext();
}
