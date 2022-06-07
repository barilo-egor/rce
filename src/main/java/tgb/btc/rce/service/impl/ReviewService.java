package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Review;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.ReviewRepository;

import java.util.List;

@Service
public class ReviewService extends BasePersistService<Review> {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(BaseRepository<Review> baseRepository, ReviewRepository reviewRepository) {
        super(baseRepository);
        this.reviewRepository = reviewRepository;
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
}
