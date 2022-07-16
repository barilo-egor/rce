package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {

    List<Review> findAllByIsPublished(Boolean isPublished);
}
